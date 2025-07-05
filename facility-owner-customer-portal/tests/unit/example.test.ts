import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import { createVuetify } from 'vuetify'
import { createPinia } from 'pinia'
import MetricCard from '@/components/MetricCard.vue'

// Create Vuetify instance for testing
const vuetify = createVuetify()
const pinia = createPinia()

describe('MetricCard Component', () => {
  it('renders metric card with correct data', () => {
    const wrapper = mount(MetricCard, {
      props: {
        title: 'Customers',
        value: '234',
        change: '+12',
        icon: 'mdi-account-group',
        color: 'primary'
      },
      global: {
        plugins: [vuetify, pinia]
      }
    })

    expect(wrapper.text()).toContain('Customers')
    expect(wrapper.text()).toContain('234')
    expect(wrapper.text()).toContain('+12')
  })

  it('applies correct color class', () => {
    const wrapper = mount(MetricCard, {
      props: {
        title: 'Revenue',
        value: '$28,450',
        change: '+$2,340',
        icon: 'mdi-currency-usd',
        color: 'success'
      },
      global: {
        plugins: [vuetify, pinia]
      }
    })

    expect(wrapper.classes()).toContain('text-success')
  })
})