function sendData() {
    var name = document.getElementById('idNameFurniture').value;
    var info = document.getElementById('idInfo').value;
    var material = document.getElementById('idMaterial').value;
    var dimensions = document.getElementById('idDimensions').value;
    var price = parseFloat(document.getElementById('idPrice').value);
    var furnitureImage = document.getElementById('idFurnitureImage').files;
    var storeId = document.getElementById('idStoreId').value;
    var typeId = document.getElementById('idTypeId').value;

    var store = null;
    var type = null;

    if (storeId != "") {
        store = getObjectFromParse("Store", storeId);
    } else {
        var nameStore = document.getElementById('idNameStore').value;
        var address = document.getElementById('idAddress').value;
        var email = document.getElementById('idEmail').value;
        var customersPhone = document.getElementById('idCustomersphone').value;
        var webpage = document.getElementById('idWebpage').value;
        var workingHours = document.getElementById('idWorkingHours').value;
        var logo = document.getElementById('idLogo').files;
        var latitude = parseFloat(document.getElementById('idLatitude').value);
        var longitude = parseFloat(document.getElementById('idLongitude').value);

        if (nameStore != "" && address != "" && email != "" && customersPhone != "" && webpage != "" && workingHours != "" && isFinite(latitude) && isFinite(longitude) && logo.length > 0) {

            var point = new Parse.GeoPoint({ latitude: latitude, longitude: longitude });
            var file = new Parse.File("logo.png", logo[0], "image/png");

            var StoreObject = Parse.Object.extend("Store");
            store = new StoreObject();

            store.set("name", nameStore);
            store.set("address", address);
            store.set("customersPhone", customersPhone);
            store.set("email", email);
            store.set("webpage", webpage);
            store.set("workingHours", workingHours);
            store.set("location", point);
            store.set("logo", file);

            store.save(null, {
                success: function (store) {
                    // Execute any logic that should take place after the object is saved.
                    alert('New object created with objectId: ' + store.id);
                },
                error: function (store, error) {
                    // Execute any logic that should take place if the save fails.
                    // error is a Parse.Error with an error code and message.
                    alert('Failed to create new object, with error code: ' + error.message);
                }
            });
        } else {
            alert('Fill every field!');
        }
    }

    if (typeId != "") {
        type = getObjectFromParse("Furniture", typeId);
    } else {
        var typeName = document.getElementById('idNameType').value;
        var typeImage = document.getElementById('idTypeImage').files;

        if (typeName != "" && typeImage.length > 0) {

            var TypeObject = Parse.Object.extend("Furniture");
            type = new TypeObject();

            var fileType = new Parse.File("logo.png", typeImage[0], "image/png");
            type.set("type", typeName);
            type.set("icon", fileType);

            type.save(null, {
                success: function (type) {
                    // Execute any logic that should take place after the object is saved.
                    alert('New object created with objectId: ' + type.id);
                },
                error: function (type, error) {
                    // Execute any logic that should take place if the save fails.
                    // error is a Parse.Error with an error code and message.
                    alert('Failed to create new object, with error code: ' + error.message);
                }
            });
        } else {
            alert('Fill every field!');
        }
    }

    if (name != "" && dimensions != "" && info != "" && material != "" && isFinite(price) && (type != "" || type != null) && (store != "" || store != null) && furnitureImage.length > 0) {
        var FurnitureObgect = Parse.Object.extend("FurnitureItems");
        var furniture = new FurnitureObgect();

        var fileFurniture = new Parse.File("logo.png", furnitureImage[0], "image/png");
        furniture.set("name", name);
        furniture.set("dimensions", dimensions);
        furniture.set("info", info);
        furniture.set("material", material);
        furniture.set("price", price);
        furniture.set("drawable", fileFurniture);
        furniture.set("furnitureId", type);
        furniture.set("store", store);

        furniture.save(null, {
            success: function (type) {
                // Execute any logic that should take place after the object is saved.
                alert('New object created with objectId: ' + type.id);
                location.reload();
            },
            error: function (type, error) {
                // Execute any logic that should take place if the save fails.
                // error is a Parse.Error with an error code and message.
                alert('Failed to create new object, with error code: ' + error.message);
            }
        });
    } else {
        alert('Fill every field correct!');
    }
}

function getObjectFromParse(nameObject, id) {
    var query = new Parse.Object(nameObject);
    query.id = id;
    return query
}

function loadData() {
    var container = document.getElementById("container");
    var divType = document.createElement("div");
    divType.setAttribute("class", "container-item vertical-top");

    var TypesObject = Parse.Object.extend("Furniture");
    var query = new Parse.Query(TypesObject);

    query.find({
        success: function (types) {
            var par = document.createElement("p");
            var text = document.createTextNode("Types");
            par.appendChild(text);
            divType.appendChild(par);

            for (var i = 0; i < types.length; i++) {
                var object = types[i];
                var newElement = document.createElement("p");
                var newText = document.createTextNode(object.id + ' - ' + object.get('type'));
                newElement.appendChild(newText);
                divType.appendChild(newElement);
            }

            container.appendChild(divType);
        },
        error: function (object, error) {
            alert(error);
        }
    });

    var divStore = document.createElement("div");
    divStore.setAttribute("class", "container-item vertical-top");

    var StoreObject = Parse.Object.extend("Store");
    var query2 = new Parse.Query(StoreObject);

    query2.find({
        success: function (stores) {
            var par = document.createElement("p");
            var text = document.createTextNode("Stores");
            par.appendChild(text);
            divStore.appendChild(par);

            for (var i = 0; i < stores.length; i++) {
                var object = stores[i];
                var newElement = document.createElement("p");
                var newText = document.createTextNode(object.id + ' - ' + object.get('name'));
                newElement.appendChild(newText);
                divStore.appendChild(newElement);
            }

            container.appendChild(divStore);
        },
        error: function (object, error) {
            alert(error);
        }
    });

    var divFurniture = document.createElement("div");
    divFurniture.setAttribute("class", "container-item vertical-top");

    var FurnitureObject = Parse.Object.extend("FurnitureItems");
    var query3 = new Parse.Query(FurnitureObject);

    query3.find({
        success: function (furnitures) {
            var par = document.createElement("p");
            var text = document.createTextNode("Furnitures");
            par.appendChild(text);
            divFurniture.appendChild(par);

            for (var i = 0; i < furnitures.length; i++) {
                var object = furnitures[i];
                var newElement = document.createElement("p");
                var newText = document.createTextNode(object.id + ' - ' + object.get('name'));
                newElement.appendChild(newText);
                divFurniture.appendChild(newElement);
            }

            container.appendChild(divFurniture);
        },
        error: function (object, error) {
            alert(error);
        }
    });
}

function deleteItem() {
    var idItem = document.getElementById('idDeleteItem').value.trim();

    var FurnitureObj = Parse.Object.extend("FurnitureItems");
    var query = new Parse.Query(FurnitureObj);
    query.get(idItem, {
        success: function (item) {
            item.destroy({
                success: function (myObject) {
                    alert("Item deleted successfully.");
                    location.reload();
                },
                error: function (myObject, error) {
                    alert("Deletion revoked");
                }
            });
        },
        error: function (object, error) {
            alert("Couldn't find such item. Try again!");
        }
    });
}