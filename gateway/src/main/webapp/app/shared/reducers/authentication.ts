import axios, { AxiosResponse } from 'axios';
import { Storage } from 'react-jhipster';
import { createAsyncThunk, createSlice } from '@reduxjs/toolkit';
import { serializeAxiosError } from './reducer.utils';

import { AppThunk } from 'app/config/store';
import { setLocale } from 'app/shared/reducers/locale';

import { User } from 'oidc-client-ts';

const AUTH_TOKEN_KEY = 'jhi-authenticationToken';

export const initialState = {
  loading: false,
  isAuthenticated: false,
  account: {} as any,
  errorMessage: null as unknown as string, // Errors returned from server side
  redirectMessage: null as unknown as string,
  sessionHasBeenFetched: false,
  logoutUrl: null as unknown as string,
};

export type AuthenticationState = Readonly<typeof initialState>;

// Actions

export const getSession = (): AppThunk => async (dispatch, getState) => {
  await dispatch(getAccount());

  const { account } = getState().authentication;
  if (account && account.langKey) {
    const langKey = Storage.session.get('locale', account.langKey);
    dispatch(setLocale(langKey));
  }
};


// export const getSessionFromOIDC = (): AppThunk => async (dispatch, getState) => {
//   await dispatch(getAccountFromOIDC());

//   const { account } = getState().authentication;
//   if (account && account.langKey) {
//     const langKey = Storage.session.get('locale', account.langKey);
//     dispatch(setLocale(langKey));
//   }
// };


export const getSessionFromOIDC: () => AppThunk =
  () => 
  async dispatch => {

    const  result = await dispatch(getAccountFromOIDC());    
    const response = result.payload as User;    
    const bearerToken = response?.access_token;
    if (bearerToken){
      Storage.session.set(AUTH_TOKEN_KEY, bearerToken);
    }   
  };

export const getAccountFromOIDC = createAsyncThunk('authentication/get_account_from_oidc',
  () => {
    const userStr = Storage.session.get("oidc.user:http://localhost:9080/auth/realms/jhipster:web_app");
    if (userStr) {

     const userOidc = User.fromStorageString(JSON.stringify(userStr));
     userOidc.profile.authorities = userOidc.profile.roles;
    return userOidc.profile;
    
    } else { return null; }
  },
  {}
);

export const getAccount = createAsyncThunk('authentication/get_account', async () => axios.get<any>('api/account'), {
  serializeError: serializeAxiosError,
});

export const logoutServer = createAsyncThunk('authentication/logout', async () => axios.post<any>('api/logout', {}), {
  serializeError: serializeAxiosError,
});

export const logoutServerOIDC = createAsyncThunk('authentication/logout-oidc', () => null , {
  
});

export const logout: () => AppThunk = () =>  dispatch => {
  // await dispatch(logoutServer());
  // fetch new csrf token
  dispatch(getSession());
};

export const clearAuthentication = messageKey => dispatch => {
  dispatch(authError(messageKey));
  dispatch(clearAuth());
};

export const AuthenticationSlice = createSlice({
  name: 'authentication',
  initialState: initialState as AuthenticationState,
  reducers: {
    authError(state, action) {
      return {
        ...state,
        redirectMessage: action.payload,
      };
    },
    clearAuth(state) {
      return {
        ...state,
        loading: false,
        isAuthenticated: false,
      };
    },
  },
  extraReducers(builder) {
    builder
      .addCase(getAccount.rejected, (state, action) => ({
        ...state,
        loading: false,
        isAuthenticated: false,
        sessionHasBeenFetched: true,
        errorMessage: action.error.message,
      }))
      .addCase(getAccount.fulfilled, (state, action) => {
        const isAuthenticated = action.payload && action.payload.data && action.payload.data.activated;
        return {
          ...state,
          isAuthenticated,
          loading: false,
          sessionHasBeenFetched: true,
          account: action.payload.data,
        };
      })
      .addCase(logoutServer.fulfilled, (state, action) => ({
        ...initialState,
        logoutUrl: action.payload.data.logoutUrl,
      }))
      .addCase(logoutServerOIDC.fulfilled, (state, action) => ({
        ...initialState,
      }))
      .addCase(getAccount.pending, state => {
        state.loading = true;
      })
      .addCase(getAccountFromOIDC.fulfilled, (state, action) => {
        const isAuthenticated = action.payload ? true: false;
        return {
          ...state,
          isAuthenticated,
          loading: false,
          sessionHasBeenFetched: true,
          account: action.payload,
        };
      });
  },
});

export const { authError, clearAuth } = AuthenticationSlice.actions;

// Reducer
export default AuthenticationSlice.reducer;
