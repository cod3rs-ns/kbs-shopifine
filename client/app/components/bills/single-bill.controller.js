(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('SingleBillController', SingleBillController);

    SingleBillController.$inject = ['CONFIG', '$stateParams', '$localStorage', '$log', 'bills', 'billItems', 'discounts', 'productService', '_'];

    function SingleBillController(CONFIG, $stateParams, $localStorage, $log, bills, billItems, discounts, productService, _) {
        var billVm = this;

        billVm.bill = {};

        billVm.retrieveBill = retrieveBill;

        init();

        function init() {
            var id = $localStorage.user.id;
            billVm.retrieveBill(id, $stateParams.id);
        }

        function retrieveBill(userId, billId) {
            bills.getOne(userId, billId)
                .then(function (response) {
                    var attributes = response.data.attributes;

                    billVm.bill = {
                        'id': response.data.id,
                        'amountWithoutDiscounts': attributes.amount + attributes.discountAmount,
                        'amount': attributes.amount,
                        'createdAt': attributes.createdAt,
                        'discount': attributes.discount,
                        'discountAmount': attributes.discountAmount,
                        'pointsGained': attributes.pointsGained,
                        'pointsSpent': attributes.pointsSpent,
                        'status': attributes.state,
                        'items': [],
                        'discounts': []
                    };

                    discounts.retrieveFrom(CONFIG.SERVICE_BASE_URL + response.data.relationships.discounts.links.related)
                        .then(function (response) {
                            _.forEach(response.data, function (discount) {
                               billVm.bill.discounts.push({
                                   'type': discount.attributes.type,
                                   'discount': discount.attributes.discount
                               });
                            });
                        })
                        .catch(function (data) {
                            $log.error(data);
                        });

                    billItems.retrieveAllFrom(CONFIG.SERVICE_BASE_URL + response.data.relationships.items.links.related)
                        .then(function (response) {
                            _.forEach(response.data, function (item) {
                                var billItem = {
                                    'id': item.id,
                                    'ordinal': item.attributes.ordinal,
                                    'quantity': item.attributes.quantity,
                                    'price': item.attributes.price,
                                    'amount': item.attributes.amount,
                                    'discount': item.attributes.discount,
                                    'discounts': [],
                                    'imageUrl': '',
                                    'name': ''
                                };

                                productService.getOne(item.relationships.product.data.id)
                                    .then(function (response) {
                                        billItem.imageUrl = response.data.attributes.imageUrl;
                                        billItem.name = response.data.attributes.name;
                                    })
                                    .catch(function (data) {
                                        $log.error(data);
                                    });

                                discounts.retrieveFrom(CONFIG.SERVICE_BASE_URL + item.relationships.discounts.links.related)
                                    .then(function (response) {
                                        _.forEach(response.data, function (discount) {
                                            billItem.discounts.push({
                                                'type': discount.attributes.type,
                                                'discount': discount.attributes.discount
                                            });
                                        });
                                    })
                                    .catch(function (data) {
                                        $log.error(data);
                                    });

                                billVm.bill.items.push(billItem);
                            })
                        })
                        .catch(function (data) {
                            $log.error(data);
                        });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

    }

})();
