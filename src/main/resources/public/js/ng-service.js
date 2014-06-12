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

myApp.service('scheduler', function($http) {
    function commonErrorHandler(data) {
        angular.element('#error').empty().append('<font color="red">' + data + '</font>');
    };

    return {
        connect: function(props, successCallback) {
            $http.post('http://localhost:4567/connect', props)
                .success(successCallback)
                .error(commonErrorHandler);
        },
        disconnect: function(successCallback) {
            $http.post('http://localhost:4567/disconnect')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getTriggers: function(triggerName, successCallback) {
            $http.get('http://localhost:4567/triggers', {'triggerName': triggerName})
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getJobs: function(jobName, successCallback) {
            $http.get('http://localhost:4567/jobs', {'jobName': jobName})
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getExecutingJobs: function(successCallback) {
            $http.get('http://localhost:4567/executingJobs')
                .success(successCallback)
                .error(commonErrorHandler);
        }
    }
});