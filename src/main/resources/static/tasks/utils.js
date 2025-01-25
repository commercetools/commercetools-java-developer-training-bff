function handleResponse(response) {
    if (response.status === 501) {
        return response.json().then(error => {
            throw new Error('The function is not implemented on the server.');
        });
    }
    if (!response.ok) {
        return response.json().then(error => {
            throw new Error('Something went wrong with the request.');
        });
    }
    return response.json();
}