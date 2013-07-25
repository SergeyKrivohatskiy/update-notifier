function getEmail() {
    gapi.client.load('plus', 'v1', function() {
        gapi.client.plus.people.get( {'userId' : 'me'} ).execute(function(resp) {
            // Shows profile information
            console.log(resp);
        })
    });
}
function signinCallback(authResult) {
    if (authResult['access_token']) {
        // Successfully authorized
        // Hide the sign-in button now that the user is authorized, for example:
    document.getElementById('signinButton').setAttribute('style', 'display: none');
//    var access_token = gapi.auth.getToken().access_token
//    var id_token = gapi.auth.getToken().id_token
    gapi.auth.setToken(authResult);

        gapi.client.load('oauth2', 'v2', function() {
            gapi.client.oauth2.userinfo.get().execute(function(resp) {
                // Shows user email
//                console.log(resp.email);
                var name = resp.name
                var gender = resp.gender
                var email = resp.email;
//                getEmail();
                $('<form action="signin" method="POST">' +
                    '<input type="hidden" name="email" value="' + email + '">' +
                    '</form>').submit();
            })
        });
//    $('#unique').text(tok)
    } else if (authResult['error']) {
//    document.getElementById('res').setAttribute('style', 'display: none')
        // There was an error.
        // Possible error codes:
        //   "access_denied" - User denied access to your app
        //   "immediate_failed" - Could not automatically log in the user
        // console.log('There was an error: ' + authResult['error']);
    }
}