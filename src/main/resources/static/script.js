function searchProduct() {
        const keyword = document.getElementById('searchKeyword').value.trim();
        const resultsDiv = document.getElementById('results');

        resultsDiv.innerHTML = '';

        if (!keyword) {
            resultsDiv.innerHTML = 'Please enter a search keyword.';
            return;
        }
        const url = `http://localhost:8080/api/products?keyword=${keyword}`;

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log(data.facets);
                if (data.facets && data.facets.length > 0) { //Check if the response has facets
                    data.facets.forEach(facet => {              // Process each facet
                        let facetDiv = document.createElement('div');
                        facetDiv.classList.add('dropdown');
                        const uniqueFacetId = `facet-${facet.name}`;

                        //create a dropdown for each facet
                        facetDiv.innerHTML = `
                            <button onclick="toggleShow('${uniqueFacetId}')" class="dropbtn">${facet.name}</button>`;
                            const bucketsDiv = document.createElement('div');
                            bucketsDiv.id = `buckets-${uniqueFacetId}`;
                            bucketsDiv.classList.add('dropdown-content');
                            facet.buckets.forEach(bucket => {
                              bucketsDiv.innerHTML +=`<a href=#${bucket.key}>${bucket.key} - ${bucket.count}</a>`;
                            });
                            facetDiv.appendChild(bucketsDiv);
                        resultsDiv.appendChild(facetDiv);
                    });
                } else {
                    resultsDiv.innerHTML = 'No facets found for this search term.';
                }

                // display the results
                if (data.results && data.results.length > 0) {
                    console.log(data.results);
                    data.results.forEach(product => {
                        const productDiv = document.createElement('div');
                        productDiv.classList.add('product');
                        productDiv.innerHTML = `
                            <h3>${product.productProjection.name["en-US"]}</h3>
                            <p>Price: ${product.productProjection.masterVariant.price.value.currencyCode} ${product.productProjection.masterVariant.price.value.centAmount}</p>
                            <p>${product.productProjection.description["en-US"]}</p>

                        `;
                        resultsDiv.appendChild(productDiv);
                    });
                } else {
                    resultsDiv.innerHTML = 'No products found for this search term.';
                }
            })
            .catch(error => {
                resultsDiv.innerHTML = 'Error getting data from backend.';
                console.error('Error getting products:', error);
            });
    }
    function toggleShow(facetId) {
      document.getElementById(`buckets-${facetId}`).classList.toggle("show");
    }