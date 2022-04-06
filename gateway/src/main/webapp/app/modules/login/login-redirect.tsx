import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAuth } from "react-oidc-context";

export const LoginRedirect = props => {
  const auth = useAuth();
  useEffect(() => {
    localStorage.setItem(REDIRECT_URL, props.location.state.from.pathname);
    // window.location.reload();
    auth.signinRedirect();
  });

  return null;
};

export default LoginRedirect;
