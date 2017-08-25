(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('ShoppingCartController', ShoppingCartController);

    ShoppingCartController.$inject = ['$log', '$localStorage', 'bills', 'billItems', '_'];

    function ShoppingCartController($log, $localStorage, bills, billItems, _) {
        var cartVm = this;

        cartVm.$storage = $localStorage;

        cartVm.confirm = orderItems;

        function orderItems() {
            var userId = $localStorage.user.id;

            var bill = {
                'data': {
                    'type': 'bills',
                    'attributes': {
                        'state': 'ORDERED',
                        'totalItems': _.size(cartVm.$storage.items)
                    },
                    'relationships': {
                        'customer': {
                            'data': {
                                'type': 'users',
                                'id': userId
                            }
                        }
                    }
                }
            };

            bills.create($localStorage.user.id, bill)
                .then(function (response) {
                    _.forEach(cartVm.$storage.items, function (item) {
                        var billItem = {
                            'data': {
                                'type': 'bill-items',
                                'attributes': {
                                    'price': item.product.price,
                                    'quantity': _.parseInt(item.quantity),
                                    'discount': 0
                                },
                                'relationships': {
                                    'product': {
                                        'data': {
                                            'type': 'products',
                                            'id': item.product.id
                                        }
                                    },
                                    'bill': {
                                        'data': {
                                            'type': 'bills',
                                            'id': response.data.id
                                        }
                                    }
                                }
                            }
                        };

                        billItems.create(userId, response.data.id, billItem)
                            .then(function (response) {
                                // TODO Handle response
                                $log.info(response.data);
                            })
                            .catch(function (data) {
                                $log.error(data);
                            });
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }
    }
})();
