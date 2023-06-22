// add target scope to subscription
targetScope = 'subscription'

// add parameters for name and location that defaults to koreacentral
param name string
param rg_name string
param location string = 'koreacentral'

resource rg 'Microsoft.Resources/resourceGroups@2021-04-01' = {
  name: rg_name
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

//test

