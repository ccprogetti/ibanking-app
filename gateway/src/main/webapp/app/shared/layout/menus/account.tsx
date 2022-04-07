import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { ButtonDropdown, DropdownItem, DropdownMenu, DropdownToggle } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate, translate } from 'react-jhipster';
import { getLoginUrl } from 'app/shared/util/url-utils';
import { NavDropdown } from './menu-components';
import { useAuth } from 'react-oidc-context';

const accountMenuItemsAuthenticated = () => (
  <>
    <MenuItem icon="sign-out-alt" to="/logout" data-cy="logout">
      <Translate contentKey="global.menu.account.logout">Sign out</Translate>
    </MenuItem>
  </>
);

const accountMenuItems = () => (
  <>
     <MenuItem icon="sign-in-alt" to="/login" data-cy="login">
      <Translate contentKey="global.menu.account.login">Sign in</Translate>
    </MenuItem>
  </>
);

export const AccountMenu = ({ isAuthenticated = false }) =>{   

  const auth = useAuth();
  // ButtonDropdown open state
  const [dropdownOpen, setOpen] = React.useState(false);
  
  return (
  <ButtonDropdown  toggle={() => { setOpen(!dropdownOpen) }} isOpen={dropdownOpen}>
        <DropdownToggle caret>
        {translate('global.menu.account.main')}
        </DropdownToggle>
        <DropdownMenu>
        <DropdownItem hidden={auth.isAuthenticated} onClick={() => {auth.signinRedirect();}}>Login</DropdownItem> 
        <DropdownItem hidden={!auth.isAuthenticated} onClick={() => {auth.signoutRedirect();}}>Logout</DropdownItem>          
        </DropdownMenu>
      </ButtonDropdown>);  
  
  
  // <NavDropdown icon="user" name={translate('global.menu.account.main')} id="account-menu" data-cy="accountMenu">
  //   {isAuthenticated ? accountMenuItemsAuthenticated() : accountMenuItems()}

  // </NavDropdown>
};

export default AccountMenu;
