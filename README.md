# InvestSmart Pro

## Overview
InvestSmart Pro is a comprehensive investment portfolio management system that helps investment firms manage client investments, track financial advisor relationships, and automate portfolio analysis. The system implements advanced algorithms for portfolio recommendations and advisor grouping analysis.

## Key Features
- **Portfolio Management**
  - Client investment tracking
  - Financial advisor management
  - Stock transaction processing
  - Dividend reinvestment with fractional share handling
  - Automated sector allocation monitoring

- **Machine Learning & Analytics**
  - Stock recommendations using cosine similarity
  - Advisor grouping through k-means clustering
  - Portfolio divergence detection
  - Investment pattern recognition
  - Sector distribution analysis

- **Technical Implementation**
  - Custom data structures for efficient portfolio tracking
  - SQL-optimized queries for complex transactions
  - Robust dividend processing system
  - Automated testing framework

## Technology Stack
- **Backend**: Java
- **Database**: MySQL
- **Testing**: JUnit, JMeter
- **Algorithms**: K-means Clustering, Cosine Similarity
- **Design Patterns**: Factory, Observer

## System Architecture

### Database Schema
- Clients
- Financial Advisors
- Investment Accounts
- Stock Holdings
- Transactions
- Investment Profiles
- Sectors

### Core Components
1. **Investment Management Module**
   - Account creation and management
   - Stock trading operations
   - Dividend processing
   - Portfolio valuation

2. **Analytics Engine**
   - Portfolio similarity analysis
   - Investment recommendations
   - Advisor grouping
   - Divergence detection

3. **Data Access Layer**
   - Optimized SQL queries
   - Transaction management
   - Data persistence
   - Cache management

## Setup Instructions

### Prerequisites
- Java 11 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

### Installation Steps
1. Clone the repository
```bash
git clone https://github.com/boradRushil/Investsmart-Pro.git
```

2. Configure MySQL database
```sql
CREATE DATABASE investsmart;
```

3. Update database configuration in `application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/investsmart
spring.datasource.username=your_username
spring.datasource.password=your_password
```

4. Build the project
```bash
mvn clean install
```

## Testing

### Unit Tests
- Comprehensive test suite covering core functionality
- 95% code coverage
- Automated test cases for edge scenarios

### Performance Testing
- JMeter test suite for load testing
- Support for 1000+ concurrent operations
- Response time monitoring
- Database query optimization validation

## API Documentation

### Core Methods
1. `defineSector(String sectorName)`
   - Defines investment sectors
   - Used for portfolio categorization

2. `defineStock(String companyName, String stockSymbol, String sector)`
   - Registers new stocks in the system
   - Associates stocks with sectors

3. `tradeShares(int account, String stockSymbol, int sharesExchanged)`
   - Processes buy/sell transactions
   - Handles cash balance updates

4. `disburseDividend(String stockSymbol, double dividendPerShare)`
   - Processes dividend payments
   - Manages fractional share reinvestment

### Analytics Methods
1. `stockRecommendations(int accountId, int maxRecommendations, int numComparators)`
   - Generates buy/sell recommendations
   - Uses cosine similarity for portfolio comparison

2. `advisorGroups(double tolerance, int maxGroups)`
   - Groups similar financial advisors
   - Implements k-means clustering

## Performance Metrics
- Database query optimization achieving sub-second response times
- Support for high-volume transaction processing
- Efficient handling of fractional share calculations
- Real-time portfolio analysis and recommendations

## Future Enhancements
- Real-time market data integration
- Advanced risk analysis metrics
- Mobile application interface
- Enhanced reporting capabilities
- API gateway implementation

## License
This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
