import { configureStore } from '@reduxjs/toolkit';
import { setupListeners } from '@reduxjs/toolkit/query';
import authReducer from './slices/authSlice';
import searchReducer from './slices/searchSlice';
import facilityReducer from './slices/facilitySlice';
import bookingReducer from './slices/bookingSlice';
import uiReducer from './slices/uiSlice';
import { api } from './api';

export const store = configureStore({
  reducer: {
    auth: authReducer,
    search: searchReducer,
    facility: facilityReducer,
    booking: bookingReducer,
    ui: uiReducer,
    [api.reducerPath]: api.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: ['auth/setCredentials', 'search/setLocation'],
      },
    }).concat(api.middleware),
  devTools: process.env.NODE_ENV !== 'production',
});

setupListeners(store.dispatch);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;