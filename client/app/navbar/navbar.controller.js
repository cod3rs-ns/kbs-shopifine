(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$localStorage', '_'];

    function NavbarController($localStorage, _) {
        var navbarVm = this;

        navbarVm.$storage = $localStorage;

        navbarVm.isGuest = isGuest;
        navbarVm.isCustomer = isCustomer;
        navbarVm.isManager = isManager;
        navbarVm.isSalesman = isSalesman;
        navbarVm.total = shoppingCartSum;

        function isGuest() {
            return _.isEmpty(navbarVm.$storage.user);
        }

        function isCustomer() {
            return isUser("CUSTOMER");
        }

        function isManager() {
            return isUser("SALES_MANAGER");
        }

        function isSalesman() {
            return isUser("SALESMAN");
        }

        function isUser(role) {
            return !isGuest() && navbarVm.$storage.user.role === role;
        }

        function shoppingCartSum() {
            return _.sumBy(navbarVm.$storage.items, function(item) {
                return item.quantity * item.product.price;
            });
        }
    }

})();
