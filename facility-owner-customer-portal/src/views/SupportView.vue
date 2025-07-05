<template>
  <v-container>
    <v-row>
      <v-col cols="12">
        <h1 class="text-h4 mb-6">Support Center</h1>
        
        <v-row>
          <v-col cols="12" md="8">
            <v-card>
              <v-card-title>Contact Support</v-card-title>
              <v-card-text>
                <v-form>
                  <v-text-field
                    label="Subject"
                    variant="outlined"
                    class="mb-3"
                  />
                  
                  <v-textarea
                    label="Message"
                    variant="outlined"
                    rows="6"
                    class="mb-3"
                  />
                  
                  <v-btn color="primary" size="large">
                    Send Message
                  </v-btn>
                </v-form>
              </v-card-text>
            </v-card>
          </v-col>
          
          <v-col cols="12" md="4">
            <v-card class="mb-4">
              <v-card-title>Quick Help</v-card-title>
              <v-card-text>
                <v-list>
                  <v-list-item>
                    <v-list-item-title>📞 Phone Support</v-list-item-title>
                    <v-list-item-subtitle>(555) 123-HELP</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>📧 Email Support</v-list-item-title>
                    <v-list-item-subtitle>support@exalt.com</v-list-item-subtitle>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>🕒 Hours</v-list-item-title>
                    <v-list-item-subtitle>24/7 Available</v-list-item-subtitle>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
            
            <v-card>
              <v-card-title>Documentation</v-card-title>
              <v-card-text>
                <v-list>
                  <v-list-item>
                    <v-list-item-title>User Guide</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>FAQ</v-list-item-title>
                  </v-list-item>
                  <v-list-item>
                    <v-list-item-title>Video Tutorials</v-list-item-title>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>
          </v-col>
        </v-row>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// Support view implementation
</script>