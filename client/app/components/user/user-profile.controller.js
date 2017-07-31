(function () {
    'use strict';

    angular
        .module('shopifine-app')
        .controller('UserProfileController', UserProfileController);

    UserProfileController.$inject = ['$log', '$localStorage', 'users'];

    function UserProfileController($log, $localStorage, users) {
        var profileVm = this;

        profileVm.user = {};

        profileVm.me = me;

        init();

        function init() {
            profileVm.me();
        }

        function me() {
            var id = $localStorage.user.id;
            users.findBy(id)
                .then(function (response) {
                    $log.info(response);
                    profileVm.user = response.data.attributes;
                })
                .catch(function (data) {
                    $log.error(data);
                });
        }

    }
})();
