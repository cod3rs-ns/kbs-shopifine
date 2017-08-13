(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('NavbarController', NavbarController);

    NavbarController.$inject = ['$localStorage', '$location', '_'];

    function NavbarController($localStorage, $location, _) {
        var navbarVm = this;

        navbarVm.$storage = $localStorage;

        navbarVm.total = shoppingCartSum;
        navbarVm.logout = logout;

        navbarVm.isGuest = isGuest;
        navbarVm.isCustomer = isCustomer;
        navbarVm.isManager = isManager;
        navbarVm.isSalesman = isSalesman;

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

        function logout() {
            $localStorage.$reset();
            $location.path('/login');
        }
    }

})();
