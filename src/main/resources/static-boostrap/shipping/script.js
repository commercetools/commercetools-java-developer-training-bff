function getShippingMethods() {
    const key = document.getElementById('key').value.trim();
    const resultsDiv = document.getElementById('results');

    resultsDiv.innerHTML = '';

    if (!key) {
        const url = `http://localhost:8080/api/shippingmethods/`;
        fetch(url)
                .then(response => response.json())
                .then(shippingMethods => {
                    // display the results

                    if (shippingMethods && shippingMethods.length > 0) {
                        console.log(shippingMethods);
                        console.log(typeof(shippingMethods));
                        shippingMethods.forEach(shippingMethod => {
                            const shippingMethodDiv = document.createElement('div');
                            shippingMethodDiv.classList.add('shipping-method');
                            shippingMethodDiv.innerHTML = `
                                <h3>${shippingMethod.name}</h3>`
                            if (shippingMethod.zoneRates && shippingMethod.zoneRates.length > 0) {
                                shippingMethod.zoneRates.forEach(zoneRate => {
                                    shippingMethodDiv.innerHTML += `<h4>${zoneRate.zone.id}</h4>`;
                                    zoneRate.shippingRates.forEach(shippingRate => {
                                            shippingMethodDiv.innerHTML += `<p>${shippingRate.price.currencyCode}${shippingRate.price.centAmount}</p>`;
                                    });
                                });
                            }
                            resultsDiv.appendChild(shippingMethodDiv);
                        });
                    } else {
                        resultsDiv.innerHTML = 'No shipping methods found.';
                    }
                })
                .catch(error => {
                    resultsDiv.innerHTML = 'Error getting data from backend.';
                    console.error('Error getting shipping methods:', error);
                });
    }
    else {

        const url = `http://localhost:8080/api/shippingmethods/${key}`;

        fetch(url)
                    .then(response => response.json())
                    .then(data => {
                        if (data) {
                            console.log(data);
                            resultsDiv.innerHTML = `<h3>${data.name}</h3>`;

                            if (data.zoneRates && data.zoneRates.length > 0) {
                                data.zoneRates.forEach(zoneRate => {
                                    resultsDiv.innerHTML += `<h4>${zoneRate.zone.id}</h4>`;
                                    zoneRate.shippingRates.forEach(shippingRate => {
                                            resultsDiv.innerHTML += `<p>${shippingRate.price.currencyCode}${shippingRate.price.centAmount}</p>`;
                                        });
                                });
                            } else {
                                resultsDiv.innerHTML += '<p>No zone rates available.</p>';
                            }
                        } else {
                            resultsDiv.innerHTML = 'Shipping method doesn\'t exist.';
                        }
                    })
                    .catch(error => {
                        resultsDiv.innerHTML = 'Error getting data from backend.';
                        console.error('Error getting shipping method:', error);
                    });
            }
        }

function checkShippingMethod() {
    const key = document.getElementById('key').value.trim();
    const resultsDiv = document.getElementById('results');

    resultsDiv.innerHTML = '';

    if (!key) {
        resultsDiv.innerHTML = 'Enter the key to check.';
        return;
    }
    else {
          const url = `http://localhost:8080/api/shippingmethods/exists/${key}`;
              fetch(url, { method: 'HEAD' })
                  .then(response => {
                      if (response.ok){
                        resultsDiv.innerHTML = 'Shipping Method exists';
                      }
                      else if (response.status === 404){
                          resultsDiv.innerHTML = 'Shipping Method doesn\'t exist';
                      }
                      else {
                        resultsDiv.innerHTML = `Error: ${response.statusText}`;
                      }

                  })
                  .catch(error => {
                      resultsDiv.innerHTML = 'Error getting response from backend.';
                      console.error('Error getting shipping methods:', error);
                  });
    }
}