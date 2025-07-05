import { test, expect } from '@playwright/test'

test.describe('Facility Owner Portal E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    // Navigate to the application
    await page.goto('/')
  })

  test('should display login page for unauthenticated users', async ({ page }) => {
    // Should redirect to login page
    await expect(page).toHaveURL(/.*\/login/)
    
    // Should display login form
    await expect(page.locator('input[type="email"]')).toBeVisible()
    await expect(page.locator('input[type="password"]')).toBeVisible()
    await expect(page.locator('button[type="submit"]')).toBeVisible()
  })

  test('should login successfully with valid credentials', async ({ page }) => {
    // Fill login form
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Should redirect to dashboard
    await expect(page).toHaveURL(/.*\/dashboard/)
    
    // Should display dashboard content
    await expect(page.locator('h1')).toContainText('Customer Operations Dashboard')
  })

  test('should display facility metrics on dashboard', async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    
    // Wait for dashboard to load
    await page.waitForURL(/.*\/dashboard/)
    
    // Check for metric cards
    await expect(page.locator('[data-testid="customers-metric"]')).toBeVisible()
    await expect(page.locator('[data-testid="occupancy-metric"]')).toBeVisible()
    await expect(page.locator('[data-testid="revenue-metric"]')).toBeVisible()
    await expect(page.locator('[data-testid="rating-metric"]')).toBeVisible()
  })

  test('should navigate to customers page', async ({ page }) => {
    // Login and navigate to dashboard
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    await page.waitForURL(/.*\/dashboard/)
    
    // Click on customers navigation
    await page.click('[data-testid="nav-customers"]')
    
    // Should navigate to customers page
    await expect(page).toHaveURL(/.*\/customers/)
    
    // Should display customers table
    await expect(page.locator('[data-testid="customers-table"]')).toBeVisible()
  })

  test('should navigate to units page', async ({ page }) => {
    // Login and navigate to dashboard
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    await page.waitForURL(/.*\/dashboard/)
    
    // Click on units navigation
    await page.click('[data-testid="nav-units"]')
    
    // Should navigate to units page
    await expect(page).toHaveURL(/.*\/units/)
    
    // Should display units grid
    await expect(page.locator('[data-testid="units-grid"]')).toBeVisible()
  })

  test('should logout successfully', async ({ page }) => {
    // Login first
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    await page.waitForURL(/.*\/dashboard/)
    
    // Click user menu
    await page.click('[data-testid="user-menu"]')
    
    // Click logout
    await page.click('[data-testid="logout-button"]')
    
    // Should redirect to login page
    await expect(page).toHaveURL(/.*\/login/)
  })

  test('should handle API errors gracefully', async ({ page }) => {
    // Mock API error
    await page.route('**/api/v1/warehouses/current', route => {
      route.fulfill({
        status: 500,
        contentType: 'application/json',
        body: JSON.stringify({ message: 'Internal Server Error' })
      })
    })
    
    // Login
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    
    // Should display error message
    await expect(page.locator('[data-testid="error-snackbar"]')).toBeVisible()
    await expect(page.locator('[data-testid="error-snackbar"]')).toContainText('Failed to load facility data')
  })

  test('should be responsive on mobile devices', async ({ page }) => {
    // Set mobile viewport
    await page.setViewportSize({ width: 375, height: 667 })
    
    // Login
    await page.goto('/login')
    await page.fill('input[type="email"]', 'owner@facility.com')
    await page.fill('input[type="password"]', 'securePassword123')
    await page.click('button[type="submit"]')
    await page.waitForURL(/.*\/dashboard/)
    
    // Should display mobile navigation
    await expect(page.locator('[data-testid="mobile-nav-drawer"]')).toBeVisible()
    
    // Metric cards should stack vertically
    const metricCards = page.locator('[data-testid^="metric-card"]')
    const firstCard = metricCards.first()
    const secondCard = metricCards.nth(1)
    
    const firstCardBox = await firstCard.boundingBox()
    const secondCardBox = await secondCard.boundingBox()
    
    // Second card should be below the first card (mobile stacking)
    expect(secondCardBox?.y).toBeGreaterThan(firstCardBox?.y || 0)
  })
})