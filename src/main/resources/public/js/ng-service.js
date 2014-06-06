var myApp = angular.module('myApp', ['ngRoute']);
myApp.config(['$routeProvider', function($routeProvider) {
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
}]);

myApp.service('schedulerService', function($http) {
    return {
        connect: function(props, successCallback) {
            $http.post('http://localhost:4567/connect', props)
                .success(successCallback);
        },
        disconnect: function(successCallback) {
            $http.post('http://localhost:4567/disconnect')
                .success(successCallback);
        },
        getTriggers: function(triggerName, successCallback) {
            $http.get('http://localhost:4567/triggers', {'triggerName': triggerName})
                .success(successCallback);
        },
        getJobs: function(jobName, successCallback) {
            $http.get('http://localhost:4567/jobs', {'jobName': jobName})
                .success(successCallback);
        }
    }
});