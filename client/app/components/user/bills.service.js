(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('bills', bills);

    bills.$inject = ['$http', 'CONFIG'];

    function bills($http, CONFIG) {
        var service = {
            getBillsByUser: getBillsByUser,
            create: create
        };

        return service;

        function getBillsByUser(id) {
            return $http.get(CONFIG.SERVICE_URL + '/users/' + id + '/bills')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function create(id, bill) {
            return $http.post(CONFIG.SERVICE_URL + '/users/' + id + '/bills', bill)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

    }
})();
