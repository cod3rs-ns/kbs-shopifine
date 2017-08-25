(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('bills', bills);

    bills.$inject = ['$http', 'CONFIG'];

    function bills($http, CONFIG) {
        var service = {
            getBillsByUser: getBillsByUser,
            getAll: getAll,
            create: create,
            confirm: confirm,
            cancel: cancel
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

        function getAll() {
            return $http.get(CONFIG.SERVICE_URL + '/bills')
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

        function confirm(id) {
            return $http.put(CONFIG.SERVICE_URL + '/bills/' + id + '?state=SUCCESSFUL')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function cancel(id) {
            return $http.put(CONFIG.SERVICE_URL + '/bills/' + id + '?state=CANCELLED')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();
