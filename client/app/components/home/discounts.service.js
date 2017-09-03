(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .service('discounts', discounts);

    discounts.$inject = ['$http', 'CONFIG'];

    function discounts($http, CONFIG) {
        var service = {
            retrieveFrom: retrieveFrom,
            getAll: getAll,
            create: create,
            modify: modify,
            addProductCategory: addProductCategoryToActionDiscount,
            removeProductCategory: removeProductCategoryFromActionDiscount
        };

        return service;

        function retrieveFrom(url) {
            return $http.get(url)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function getAll() {
            return $http.get(CONFIG.SERVICE_URL + '/action-discounts')
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function create(actionDiscount) {
            return $http.post(CONFIG.SERVICE_URL + '/action-discounts', actionDiscount)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function modify(id, actionDiscount) {
            return $http.put(CONFIG.SERVICE_URL + '/action-discounts/' + id, actionDiscount)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function addProductCategoryToActionDiscount(discountId, categoryId) {
            return $http.post(CONFIG.SERVICE_URL + '/action-discounts/' + discountId + '/product-categories/' + categoryId)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }

        function removeProductCategoryFromActionDiscount(discountId, categoryId) {
            return $http.delete(CONFIG.SERVICE_URL + '/action-discounts/' + discountId + '/product-categories/' + categoryId)
                .then(function success(response) {
                    return response.data;
                })
                .catch(function error(response) {
                    throw response.data;
                });
        }
    }
})();
