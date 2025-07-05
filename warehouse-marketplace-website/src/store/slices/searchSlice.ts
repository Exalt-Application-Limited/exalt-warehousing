import { createSlice, PayloadAction } from '@reduxjs/toolkit';
import { StorageFacility, FacilitySearchParams, Coordinates } from '../../types/facility';

interface SearchState {
  searchParams: FacilitySearchParams;
  results: StorageFacility[];
  loading: boolean;
  error: string | null;
  totalResults: number;
  currentPage: number;
  hasMore: boolean;
  selectedFacilities: string[];
  userLocation: Coordinates | null;
  recentSearches: string[];
  savedSearches: SavedSearch[];
}

interface SavedSearch {
  id: string;
  name: string;
  params: FacilitySearchParams;
  createdAt: string;
}

const initialState: SearchState = {
  searchParams: {
    radius: 25,
    limit: 20,
    offset: 0,
  },
  results: [],
  loading: false,
  error: null,
  totalResults: 0,
  currentPage: 1,
  hasMore: false,
  selectedFacilities: [],
  userLocation: null,
  recentSearches: JSON.parse(localStorage.getItem('warehouse_recent_searches') || '[]'),
  savedSearches: JSON.parse(localStorage.getItem('warehouse_saved_searches') || '[]'),
};

const searchSlice = createSlice({
  name: 'search',
  initialState,
  reducers: {
    setSearchParams: (state, action: PayloadAction<Partial<FacilitySearchParams>>) => {
      state.searchParams = { ...state.searchParams, ...action.payload };
      state.currentPage = 1;
      state.searchParams.offset = 0;
    },
    setLocation: (state, action: PayloadAction<{ location: string; coordinates?: Coordinates }>) => {
      state.searchParams.location = action.payload.location;
      if (action.payload.coordinates) {
        state.searchParams.coordinates = action.payload.coordinates;
        state.userLocation = action.payload.coordinates;
      }
      
      // Add to recent searches
      if (action.payload.location && !state.recentSearches.includes(action.payload.location)) {
        state.recentSearches.unshift(action.payload.location);
        state.recentSearches = state.recentSearches.slice(0, 10);
        localStorage.setItem('warehouse_recent_searches', JSON.stringify(state.recentSearches));
      }
    },
    setUserLocation: (state, action: PayloadAction<Coordinates>) => {
      state.userLocation = action.payload;
      state.searchParams.coordinates = action.payload;
    },
    setResults: (state, action: PayloadAction<{ results: StorageFacility[]; totalResults: number; hasMore: boolean }>) => {
      if (state.currentPage === 1) {
        state.results = action.payload.results;
      } else {
        state.results = [...state.results, ...action.payload.results];
      }
      state.totalResults = action.payload.totalResults;
      state.hasMore = action.payload.hasMore;
    },
    setLoading: (state, action: PayloadAction<boolean>) => {
      state.loading = action.payload;
    },
    setError: (state, action: PayloadAction<string | null>) => {
      state.error = action.payload;
    },
    nextPage: (state) => {
      state.currentPage += 1;
      state.searchParams.offset = (state.currentPage - 1) * (state.searchParams.limit || 20);
    },
    toggleFacilitySelection: (state, action: PayloadAction<string>) => {
      const facilityId = action.payload;
      const index = state.selectedFacilities.indexOf(facilityId);
      if (index > -1) {
        state.selectedFacilities.splice(index, 1);
      } else {
        if (state.selectedFacilities.length < 3) { // Max 3 for comparison
          state.selectedFacilities.push(facilityId);
        }
      }
    },
    clearSelectedFacilities: (state) => {
      state.selectedFacilities = [];
    },
    addSavedSearch: (state, action: PayloadAction<{ name: string; params: FacilitySearchParams }>) => {
      const savedSearch: SavedSearch = {
        id: Date.now().toString(),
        name: action.payload.name,
        params: action.payload.params,
        createdAt: new Date().toISOString(),
      };
      state.savedSearches.unshift(savedSearch);
      state.savedSearches = state.savedSearches.slice(0, 20);
      localStorage.setItem('warehouse_saved_searches', JSON.stringify(state.savedSearches));
    },
    removeSavedSearch: (state, action: PayloadAction<string>) => {
      state.savedSearches = state.savedSearches.filter(search => search.id !== action.payload);
      localStorage.setItem('warehouse_saved_searches', JSON.stringify(state.savedSearches));
    },
    clearRecentSearches: (state) => {
      state.recentSearches = [];
      localStorage.removeItem('warehouse_recent_searches');
    },
    resetSearch: (state) => {
      state.searchParams = {
        radius: 25,
        limit: 20,
        offset: 0,
      };
      state.results = [];
      state.totalResults = 0;
      state.currentPage = 1;
      state.hasMore = false;
      state.selectedFacilities = [];
      state.error = null;
    },
  },
});

export const {
  setSearchParams,
  setLocation,
  setUserLocation,
  setResults,
  setLoading,
  setError,
  nextPage,
  toggleFacilitySelection,
  clearSelectedFacilities,
  addSavedSearch,
  removeSavedSearch,
  clearRecentSearches,
  resetSearch,
} = searchSlice.actions;

export default searchSlice.reducer;