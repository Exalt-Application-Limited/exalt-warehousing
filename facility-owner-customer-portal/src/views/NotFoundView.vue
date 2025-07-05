<template>
  <v-container class="fill-height">
    <v-row justify="center" align="center" class="fill-height">
      <v-col cols="12" sm="8" md="6" class="text-center">
        <div class="text-h1 text-primary mb-4">404</div>
        <div class="text-h4 mb-4">Page Not Found</div>
        <div class="text-body-1 mb-6 text-medium-emphasis">
          The page you're looking for doesn't exist or has been moved.
        </div>
        
        <v-btn
          color="primary"
          size="large"
          to="/dashboard"
          prepend-icon="mdi-home"
        >
          Go to Dashboard
        </v-btn>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
// 404 Not Found page
</script>