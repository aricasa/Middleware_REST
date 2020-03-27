let token;

const scopesMap = new Map();
scopesMap.set("read_user", "Read user details");
scopesMap.set("read_images", "Read user images");

function loginOnEnterKey(event) {
    if (event.keyCode === 13) {
        event.preventDefault();
        login();
    }
}

$(document).ready(function() {

    /** Load scopes */
    loadScopes();

    /** Send credentials on Enter key press */
    $("#username").on('keydown', loginOnEnterKey);
    $("#password").on('keydown', loginOnEnterKey);

});

function loadScopes() {
    let scopes = $("#scope").val().split(" ");
    let permissionsList = $("#permissionsList");

    scopes.forEach(function(item) {
        let permission = scopesMap.get(item);

        if (permission === undefined) {
            permission = item;
        }

        permissionsList.append('<li style="list-style-type: initial;">' + permission + "</li>")
    });
}

function login() {
    let username = $("#username");
    let password = $("#password");

    username.removeClass("invalid");
    password.removeClass("invalid");

    const user = username.val().trim();
    const pass = password.val().trim();

    if (user.length === 0) {
        username.addClass("invalid");
        M.toast({html: "Username is empty"});
        return;
    }

    if (pass.length === 0) {
        password.addClass("invalid");
        M.toast({html: "Password is empty"});
        return;
    }

    $.ajax
    ({
        type: "POST",
        url: "/sessions",
        dataType: "json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader ("Authorization", "Basic " + btoa(user + ":" + pass));
        },
        success: function (response) {
            token = response;

            $("#login").hide(500);
            $("#permissions").show(500);
        },
        error: function(jqXHR) {
            if (jqXHR.status === 401) {
                username.addClass("invalid");
                password.addClass("invalid");
                M.toast({html: "Invalid credentials"});

            } else {
                M.toast({html: "Communication error"});
            }
        }
    });
}

function grant() {
    let clientId = $("#clientId").val();
    let callback = $("#callback").val();
    let scope = $("#scope").val();
    let state = $("#state").val();

    $.ajax({
        type: "POST",
        url: "/oauth2/authorize",
        dataType: "json",
        beforeSend: function (xhr) {
            xhr.setRequestHeader("Authorization", "Bearer " + token.id);
        },
        data: JSON.stringify({
            response_type: "code",
            client_id: clientId,
            redirect_uri: callback,
            scope: scope,
            state: state
        }),
        success: function(response) {
            window.location.replace(callback + "?code=" + response.code + "&state=" + state);
        }
    });
}

function deny() {

}
