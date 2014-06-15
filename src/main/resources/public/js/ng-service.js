var myApp = angular.module('myApp', ['ngRoute', 'ui.bootstrap']);
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
        getTriggers: function(triggerName, successCallback) {
            $http.get('/triggers', {'triggerName': triggerName})
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getJobs: function(jobName, successCallback) {
            $http.get('/jobs', {'jobName': jobName})
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getExecutingJobs: function(successCallback) {
            $http.get('/executingJobs')
                .success(successCallback)
                .error(commonErrorHandler);
        }
    }
});