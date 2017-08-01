(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('UserProfileController', UserProfileController);

    UserProfileController.$inject = ['$log', '$localStorage', 'users', 'bills'];

    function UserProfileController($log, $localStorage, users, bills) {
        var profileVm = this;

        profileVm.user = undefined;

        profileVm.me = me;
        profileVm.retrieveBills = retrieveBills;

        init();

        function init() {
            profileVm.me();
        }

        function me() {
            var id = $localStorage.user.id;
            users.findBy(id)
                .then(function (response) {
                    profileVm.user = response.data.attributes;
                    if (response.data.attributes.role === 'CUSTOMER') {
                        profileVm.user.bills = [];
                        retrieveBills(id);
                    }
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

        function retrieveBills(id) {
            bills.getBillsByUser(id)
                .then(function (response) {
                    _.forEach(response.data, function(bill) {
                        profileVm.user.bills.push({
                            'createdAt': bill.attributes.createdAt,
                            'amount': bill.attributes.amount,
                            'discount': bill.attributes.discount,
                            'discountAmount': bill.attributes.discountAmount,
                            'pointsGained': bill.attributes.pointsGained,
                            'pointsSpent': bill.attributes.pointsSpent,
                            'status': bill.attributes.state
                        });
                    });
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

    }
})();
