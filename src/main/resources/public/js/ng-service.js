var myApp = angular.module('myApp', ['ngRoute', 'ui.bootstrap']);
myApp
.config(['$routeProvider', function($routeProvider) {
    $routeProvider
        .when('/', {
            templateUrl : 'connect',
            controller  : 'connectController'
        })
        .when('/mon', {
            templateUrl : 'mon',
            controller  : 'monController'
        })
        .otherwise({
            redirectTo: '/'
        });
}])
.service('scheduler', function($http) {
    function commonErrorHandler(data) {
        angular.element('#error').empty().append('<font color="red">' + data + '</font>');
    };

    return {
        connect: function(host, port, successCallback) {
            $http.post('/connect', {'host': host, 'port': port})
                .success(successCallback)
                .error(commonErrorHandler);
        },
        disconnect: function(successCallback) {
            $http.post('/disconnect')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getSchedulers: function(successCallback) {
            $http.get('/schedulers')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getTriggers: function(successCallback) {
            $http.get('/triggers')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getJobs: function(successCallback) {
            $http.get('/jobs')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getExecutingJobs: function(successCallback) {
            $http.get('/executingJobs')
                .success(successCallback)
                .error(commonErrorHandler);
        }
    }
})
.controller('connectController', ['$scope', '$location', 'scheduler',
    function($scope, $location, scheduler) {
    $scope.host = 'localhost';
    $scope.port = '1090'

    $scope.connect = function() {
        scheduler.connect($scope.host, $scope.port, function success(data) {
            $location.url('/mon');
        });
    }
}])
.controller('monController', ['$scope', 'scheduler', '$interval', '$timeout',
    function($scope, scheduler, $interval, $timeout) {

    scheduler.getTriggers(function success(data) {
        $scope.triggers = data;
    });

    scheduler.getJobs(function success(data) {
        $scope.jobs = data;
    });

    scheduler.getExecutingJobs(function success(data) {
        $scope.executingJobs = data;
    });

    var items;
    var timeline;
    var groups;

    $timeout(function() {
        scheduler.getTriggers(function success(triggers) {
            scheduler.getJobs(function success(jobs) {
                groups = loadGroups(jobs);
                loadTimeline(groups, triggers);
            });
        });

        scheduler.getSchedulers(function success(schedulers) {
            schedulerGraph(schedulers);
        });
    });

    $interval(function() {
        function findItem(name) {
            var itms = items.get({
            filter: function (item) {
                return item.name == name;
              }
            });

            return itms ? _.last(itms) : undefined;
        }

        var itemId = _.last(items.getIds());
        scheduler.getTriggers(function success(triggers) {
            _.each(triggers, function(trigger) {
                var item = findItem(trigger.name);
                if (item && item.start != trigger.nextFireTime) {
                    var triggerId = ++itemId;
                    items.add({id: triggerId,
                        group: groups.findGroupId(trigger.group),
                        content: trigger.name,
                        content: '<span id="trigger-' + triggerId + '" rel="popover" data-toggle="popover" title="' + trigger.name +
                            '" data-content="' + getContent(trigger) + '" >' + trigger.name + '</span>',
                        name: trigger.name,
                        start: trigger.nextFireTime ? trigger.nextFireTime : null,
                        type: 'point'
                    });
                }
            });
        });
    }, 5000);

    function loadGroups(jobs) {
        var groupId = 0;
        var groups = new vis.DataSet();
        _.each(jobs, function(job) {
            groups.add({id: ++groupId, content: job.name});
        });

        return {
            val: groups,
            findGroupId: function (jobName) {
                var found = _.find(groups.get(), function(group) { return group.content === jobName});
                return found ? found.id : -1;
            }
        };
    }

    function getContent(trigger) {
        var previousFireTime = trigger.previousFireTime || '';
        var nextFireTime = trigger.nextFireTime || '';
        return 'prev:' + previousFireTime + '\n'
             + 'next:' + nextFireTime;
    }

    function loadTimeline(groups, triggers) {
        var itemId = 0;
        items = new vis.DataSet();
        _.each(triggers, function(trigger) {
            var triggerId = ++itemId;
            items.add({
                id: triggerId,
                group: groups.findGroupId(trigger.group),
                content: '<span id="trigger-' + triggerId + '" rel="popover" data-toggle="popover" title="' + trigger.name +
                '" data-content="' + getContent(trigger) + '" >' + trigger.name + '</span>',
                name: trigger.name,
                start: trigger.nextFireTime, type: 'point'});
        });

        var container = document.getElementById('schedule-timeline');

        var minDate = moment().startOf('day').fromNow();
        var maxDate = moment().endOf('month').fromNow();

        var options = {
            showCurrentTime: true,
            autoResize: false,
            stack: true,
            start: new Date(Date.now() - 1000 * 60 * 60),
            height: '100%',
            min: minDate,                // lower limit of visible range
            max: maxDate,                // upper limit of visible range
            zoomMin: 1000 * 60 * 60,              // one hour in milliseconds
            zoomMax: 1000 * 60 * 60 * 24 * 31     // about three months in milliseconds
        };

        // create the timeline
        timeline = new vis.Timeline(container);
        timeline.setOptions(options);
        timeline.setGroups(groups.val);
        timeline.setItems(items);
    }

    function schedulerGraph(schedulers) {
        var nodes = [];
        var edges = [];
        var idx = 0;
        nodes.push({id: ++idx, label: 'scheduler mon', group: 'console'});
        _.each(schedulers, function(scheduler) {
            var identifier = ++idx;
            nodes.push({id: identifier, label: scheduler.name + '\n' + scheduler.instanceId, group: 'scheduler', shape: 'box', value: scheduler.executingJobs});
            edges.push({from: 1, to: identifier, color: 'gray', style: 'arrow', length: 200, width: 2});
        });

        var container = document.getElementById('schedule-instance');
        var data = {
            nodes: nodes,
            edges: edges
        };
        var options = {};
        graph = new vis.Graph(container, data, options);
    }
}]);