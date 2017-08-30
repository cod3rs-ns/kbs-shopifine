(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('ShoppingCartController', ShoppingCartController);

    ShoppingCartController.$inject = ['$log', '$state', '$timeout', '$localStorage', 'ngToast', 'bills', 'billItems', '_'];

    function ShoppingCartController($log, $state, $timeout, $localStorage, ngToast, bills, billItems, _) {
        var cartVm = this;

        cartVm.$storage = $localStorage;
        cartVm.points = 0;

        cartVm.confirm = orderItems;

        function orderItems() {
            var userId = $localStorage.user.id;

            var bill = {
                'data': {
                    'type': 'bills',
                    'attributes': {
                        'state': 'ORDERED',
                        'totalItems': _.size(cartVm.$storage.items),
                        'pointsSpent': _.parseInt(cartVm.points)
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
                    var billId = response.data.id;
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
                                            'id': billId
                                        }
                                    }
                                }
                            }
                        };

                        billItems.create(userId, billId, billItem)
                            .then(function () {
                                if (item === _.last(cartVm.$storage.items)) {
                                    ngToast.success({
                                        content: 'Bill successfully created!'
                                    });

                                    $timeout(function () {
                                        $state.transitionTo('bill', {id: billId});
                                    }, 3000);

                                }
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
