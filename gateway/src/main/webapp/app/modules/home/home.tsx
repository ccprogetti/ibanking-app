import './home.scss';

import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Row, Col, Alert } from 'reactstrap';

import { getLoginUrl, REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';

import { useAuth } from "react-oidc-context";

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      location.href = `${location.origin}${redirectURL}`;
    }
  });

  const auth = useAuth();

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

export default Home;