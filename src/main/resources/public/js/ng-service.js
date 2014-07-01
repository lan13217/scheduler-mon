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
        getSchedulers: function(successCallback) {
            $http.get('/schedulers')
                .success(successCallback)
                .error(commonErrorHandler);
        },
        getTriggers: function(triggerName, successCallback) {
            var triggersUrl = triggerName ? '/triggers/' + triggerName : '/triggers';
            $http.get(triggersUrl)
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
        },
        getTimeline: function(successCallback) {
            $http.get('/timeline')
                .success(successCallback)
                .error(commonErrorHandler);
        }
    }
});