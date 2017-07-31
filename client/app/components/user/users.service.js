(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('users', users);

    users.$inject = ['$http', 'CONFIG'];

    function users($http, CONFIG) {
        var service = {
            auth: auth,
            register: register,
            findBy: findBy
        };

        return service;

        function auth(credentials) {
            return $http.post(CONFIG.SERVICE_URL + '/users/auth', credentials)
                .then(function success(response) {
                    return response;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function register(user) {
            return $http.post(CONFIG.SERVICE_URL + '/users', user)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function findBy(id) {
            return $http.get(CONFIG.SERVICE_URL + '/users/' + id)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();
