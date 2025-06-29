import React from 'react';

interface AuthLayoutProps {
  children: React.ReactNode;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children }) => {
  return (
    <div className="min-h-screen flex">
      {/* Left Panel - Branding */}
      <div className="hidden lg:flex lg:w-1/2 bg-gradient-to-br from-blue-600 to-blue-800">
        <div className="flex flex-col justify-center items-center px-12 text-white">
          <div className="mb-8">
            <h1 className="text-4xl font-bold mb-4">Vendor Storage Portal</h1>
            <p className="text-xl text-blue-100">
              Manage your self-storage locations with ease
            </p>
          </div>
          
          <div className="space-y-6 max-w-md">
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                <span className="text-sm font-semibold">📦</span>
              </div>
              <div>
                <h3 className="font-semibold">Order Management</h3>
                <p className="text-sm text-blue-100">Process orders efficiently with real-time updates</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                <span className="text-sm font-semibold">📊</span>
              </div>
              <div>
                <h3 className="font-semibold">Analytics Dashboard</h3>
                <p className="text-sm text-blue-100">Track performance and optimize operations</p>
              </div>
            </div>
            
            <div className="flex items-center space-x-3">
              <div className="flex-shrink-0 w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center">
                <span className="text-sm font-semibold">🏷️</span>
              </div>
              <div>
                <h3 className="font-semibold">Label Generation</h3>
                <p className="text-sm text-blue-100">Print shipping labels and QR codes instantly</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      {/* Right Panel - Auth Form */}
      <div className="flex-1 flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-20 xl:px-24">
        <div className="mx-auto w-full max-w-sm lg:w-96">
          {children}
        </div>
      </div>
    </div>
  );
};

export default AuthLayout;