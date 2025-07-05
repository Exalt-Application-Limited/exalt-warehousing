import React from 'react';
import { Box, Container, Typography, Grid, Button, Card, CardContent } from '@mui/material';
import { styled } from '@mui/material/styles';
import { 
  SearchRounded, 
  SecurityRounded, 
  LocalShippingRounded, 
  ThermostateRounded,
  CalculateRounded,
  LocationOnRounded 
} from '@mui/icons-material';
import { Link } from 'react-router-dom';
import HeroSection from '../components/home/HeroSection';
import FacilitySearchForm from '../components/search/FacilitySearchForm';
import FeaturedFacilities from '../components/home/FeaturedFacilities';
import StorageSizeGuide from '../components/home/StorageSizeGuide';
import CustomerTestimonials from '../components/home/CustomerTestimonials';
import HowItWorks from '../components/home/HowItWorks';

const Section = styled(Box)(({ theme }) => ({
  padding: theme.spacing(8, 0),
  [theme.breakpoints.down('sm')]: {
    padding: theme.spacing(4, 0),
  },
}));

const FeatureCard = styled(Card)(({ theme }) => ({
  height: '100%',
  display: 'flex',
  flexDirection: 'column',
  textAlign: 'center',
  padding: theme.spacing(3),
  transition: 'transform 0.3s ease, box-shadow 0.3s ease',
  '&:hover': {
    transform: 'translateY(-4px)',
    boxShadow: theme.shadows[8],
  },
}));

const FeatureIcon = styled(Box)(({ theme }) => ({
  width: 64,
  height: 64,
  borderRadius: '50%',
  background: `linear-gradient(135deg, ${theme.palette.primary.main} 0%, ${theme.palette.primary.dark} 100%)`,
  display: 'flex',
  alignItems: 'center',
  justifyContent: 'center',
  margin: '0 auto 16px',
  color: 'white',
}));

const HomePage: React.FC = () => {
  const features = [
    {
      icon: <SecurityRounded fontSize="large" />,
      title: 'Secure Storage',
      description: 'State-of-the-art security with 24/7 monitoring, gated access, and individual unit alarms.',
    },
    {
      icon: <ThermostateRounded fontSize="large" />,
      title: 'Climate Controlled',
      description: 'Temperature and humidity controlled units to protect your valuable belongings.',
    },
    {
      icon: <LocalShippingRounded fontSize="large" />,
      title: 'Moving Services',
      description: 'Professional moving assistance, truck rentals, and packing supplies available.',
    },
    {
      icon: <LocationOnRounded fontSize="large" />,
      title: 'Convenient Locations',
      description: 'Multiple locations with easy highway access and extended operating hours.',
    },
  ];

  return (
    <Box>
      {/* Hero Section with Search */}
      <Section 
        sx={{ 
          background: 'linear-gradient(135deg, #1565c0 0%, #1976d2 50%, #42a5f5 100%)',
          color: 'white',
          position: 'relative',
          overflow: 'hidden',
          '&::before': {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: 'url(/images/storage-hero-bg.jpg) center/cover',
            opacity: 0.1,
            zIndex: 0,
          }
        }}
      >
        <Container sx={{ position: 'relative', zIndex: 1 }}>
          <Box textAlign="center" mb={6}>
            <Typography variant="h2" component="h1" gutterBottom fontWeight={700}>
              Find Secure Storage Near You
            </Typography>
            <Typography variant="h5" mb={4} sx={{ opacity: 0.9 }}>
              Compare prices, read reviews, and book storage units online
            </Typography>
            <Button
              component={Link}
              to="/calculator"
              variant="outlined"
              size="large"
              startIcon={<CalculateRounded />}
              sx={{ 
                color: 'white', 
                borderColor: 'white',
                '&:hover': {
                  backgroundColor: 'rgba(255,255,255,0.1)',
                  borderColor: 'white',
                }
              }}
            >
              Storage Size Calculator
            </Button>
          </Box>
          
          {/* Search Form */}
          <Box maxWidth={800} mx="auto">
            <FacilitySearchForm />
          </Box>
        </Container>
      </Section>
      
      {/* Features Section */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            Why Choose Our Storage
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Trusted by thousands of customers nationwide
          </Typography>
          
          <Grid container spacing={4}>
            {features.map((feature, index) => (
              <Grid item xs={12} sm={6} md={3} key={index}>
                <FeatureCard>
                  <CardContent>
                    <FeatureIcon>
                      {feature.icon}
                    </FeatureIcon>
                    <Typography variant="h6" gutterBottom fontWeight={600}>
                      {feature.title}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      {feature.description}
                    </Typography>
                  </CardContent>
                </FeatureCard>
              </Grid>
            ))}
          </Grid>
        </Container>
      </Section>
      
      {/* Featured Facilities */}
      <Section sx={{ bgcolor: 'background.paper' }}>
        <Container>
          <Box display="flex" justifyContent="space-between" alignItems="center" mb={4}>
            <Typography variant="h4" fontWeight={600}>
              Featured Storage Facilities
            </Typography>
            <Button 
              component={Link} 
              to="/search" 
              endIcon={<SearchRounded />}
              color="primary"
            >
              View All Facilities
            </Button>
          </Box>
          <FeaturedFacilities />
        </Container>
      </Section>
      
      {/* How It Works */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            How It Works
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Get storage in 3 simple steps
          </Typography>
          <HowItWorks />
        </Container>
      </Section>
      
      {/* Storage Size Guide */}
      <Section sx={{ bgcolor: 'background.paper' }}>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            Storage Size Guide
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Find the perfect size for your belongings
          </Typography>
          <StorageSizeGuide />
        </Container>
      </Section>
      
      {/* Customer Testimonials */}
      <Section>
        <Container>
          <Typography variant="h4" textAlign="center" gutterBottom fontWeight={600}>
            What Our Customers Say
          </Typography>
          <Typography variant="h6" textAlign="center" color="text.secondary" mb={6}>
            Real reviews from verified customers
          </Typography>
          <CustomerTestimonials />
        </Container>
      </Section>
      
      {/* Call to Action */}
      <Section 
        sx={{ 
          bgcolor: 'primary.main',
          color: 'white',
        }}
      >
        <Container>
          <Box textAlign="center">
            <Typography variant="h4" gutterBottom fontWeight={600}>
              Ready to Find Storage?
            </Typography>
            <Typography variant="h6" mb={4} sx={{ opacity: 0.9 }}>
              Start your search today and get moved in tomorrow
            </Typography>
            <Box display="flex" gap={2} justifyContent="center" flexWrap="wrap">
              <Button
                component={Link}
                to="/search"
                variant="contained"
                size="large"
                color="secondary"
                startIcon={<SearchRounded />}
              >
                Find Storage Now
              </Button>
              <Button
                component={Link}
                to="/calculator"
                variant="outlined"
                size="large"
                startIcon={<CalculateRounded />}
                sx={{ 
                  color: 'white', 
                  borderColor: 'white',
                  '&:hover': {
                    backgroundColor: 'rgba(255,255,255,0.1)',
                    borderColor: 'white',
                  }
                }}
              >
                Size Calculator
              </Button>
            </Box>
          </Box>
        </Container>
      </Section>
    </Box>
  );
};

export default HomePage;