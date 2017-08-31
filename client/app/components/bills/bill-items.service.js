(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('billItems', billItems);

    billItems.$inject = ['$http', 'CONFIG'];

    function billItems($http, CONFIG) {
        var service = {
            create: create,
            retrieveAllFrom: retrieveAllFrom
        };

        return service;

        function create(userId, billId, billItem) {
            return $http.post(CONFIG.SERVICE_URL + '/users/' + userId + '/bills/' + billId + '/bill-items', billItem)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function retrieveAllFrom(url) {
            return $http.get(url)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();
