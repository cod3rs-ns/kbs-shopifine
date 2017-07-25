(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('usersService', usersService);

    usersService.$inject = ['$http', 'CONFIG'];

    function usersService($http, CONFIG) {
        var service = {
            auth: auth
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
    }
})();