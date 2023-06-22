// add a namespace for the WeatherForecastController
using WebApi.Controllers;

namespace WebApiTests;

[TestClass]
public class UnitTest1
{
    [TestMethod]
    public void TestMethod1()
    {
        // add a test method that tests the GetRange method of WeatherForecastContoller
        // Path: test/WebApiTests/test/WebApiTests/Controllers/WeatherForecastControllerTests.cs
        // Arrange
        var controller = new WeatherForecastController(null);
        var range = new DateRange { Length = 5 };
    }
}