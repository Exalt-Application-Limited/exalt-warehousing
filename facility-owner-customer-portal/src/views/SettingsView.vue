<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Settings</h1>
        
        <v-row>
          <v-col cols="12" md="8">
            <v-card class="mb-4">
              <v-card-title>Facility Information</v-card-title>
              <v-card-text>
                <v-form>
                  <v-text-field
                    label="Facility Name"
                    variant="outlined"
                    model-value="Downtown Storage Center"
                    class="mb-3"
                  />
                  
                  <v-text-field
                    label="Address"
                    variant="outlined"
                    model-value="123 Main St, City, State 12345"
                    class="mb-3"
                  />
                  
                  <v-text-field
                    label="Phone Number"
                    variant="outlined"
                    model-value="(555) 123-4567"
                    class="mb-3"
                  />
                  
                  <v-btn color="primary">
                    Save Changes
                  </v-btn>
                </v-form>
              </v-card-text>
            </v-card>
            
            <v-card>
              <v-card-title>Notification Preferences</v-card-title>
              <v-card-text>
                <v-switch
                  label="Email notifications for new rentals"
                  color="primary"
                  class="mb-2"
                />
                <v-switch
                  label="SMS alerts for late payments"
                  color="primary"
                  class="mb-2"
                />
                <v-switch
                  label="Daily revenue reports"
                  color="primary"
                  class="mb-4"
                />
                
                <v-btn color="primary">
                  Update Preferences
                </v-btn>
              </v-card-text>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="4">
            <v-card>
              <v-card-title>Account Security</v-card-title>
              <v-card-text>
                <v-btn
                  color="primary"
                  variant="outlined"
                  block
                  class="mb-3"
                >
                  Change Password
                </v-btn>
                
                <v-btn
                  color="primary"
                  variant="outlined"
                  block
                  class="mb-3"
                >
                  Enable 2FA
                </v-btn>
                
                <v-btn
                  color="error"
                  variant="outlined"
                  block
                >
                  Logout
                </v-btn>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// Settings view implementation
</script>