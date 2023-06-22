// add target scope to subscription
targetScope = 'subscription'

// add parameters for name and location that defaults to koreacentral
param name string
param location string = 'koreacentral'

// add name with the prefix of 'rg-'
var rgName = 'rg-${name}'

resource rg 'Microsoft.Resources/resourceGroups@2021-04-01' = {
  name: rgName
  location: location
}

module appsvc './resources.bicep' = {
  name: 'AppService'
  scope: rg
  params: {
    name: name
    location: location
  }
}
