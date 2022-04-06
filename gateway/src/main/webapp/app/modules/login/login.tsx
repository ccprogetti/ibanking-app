import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAuth } from "react-oidc-context";
import { useAppDispatch } from 'app/config/store';

export const Login = props => {

  const auth = useAuth();
  const dispatch = useAppDispatch();  


  switch (auth.activeNavigator) {
    case "signinSilent":
      return <div>Signing you in...</div>;
    case "signoutRedirect":
      return <div>Signing you out...</div>;
    default:
      break;
      return <div>???????...</div>;
  }

  if (auth.isLoading) {
    return <div>Loading...</div>;
  }

  if (auth.error) {
    return <div>Oops... {auth.error.message}</div>;
  }

  if (auth.isAuthenticated) {
    return (
      <div>
        Hello {auth.user?.profile.sub}{" "}
        <button onClick={() => void auth.removeUser()}>Log out</button>
      </div>
    );
  }

  return <button onClick={() => void auth.signinRedirect()}>Log in</button>;

};

export default Login;
