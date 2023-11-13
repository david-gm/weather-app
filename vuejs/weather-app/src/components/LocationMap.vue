<template>
    <div class="ui container fluid">
        <div class="ui grid">
            <div class="row">
                <div class="twelve wide column">
                    <div id="map"></div>
                </div>
                <div class="four wide column" style="padding-left: 0;">
                    <h2>Locations</h2>
                    <table class="ui inverted table">
                        <thead>
                            <tr>
                                <th>Address</th>
                                <th>Coordinates</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="loc in locations">
                                <td>{{ loc.address }}</td>
                                <td>{{ formatCoordinates(loc.coordinates) }}</td>
                                <td>
                                    <button class="ui icon purple button" @click="zoomToLocation(loc)">
                                        <i class="zoom icon"></i>
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            <div class="row">
                <div class="ui sixteen wide column">
                    <input class="ui button primary" type="button" value="Get Locations" @click="getLocations()" />
                </div>
            </div>
        </div>

    </div>
</template>

<script>
import L from "leaflet";
import '@geoapify/leaflet-address-search-plugin';
import 'leaflet/dist/leaflet.css';
import '@geoapify/leaflet-address-search-plugin/dist/L.Control.GeoapifyAddressSearch.min.css';
import 'leaflet-extra-markers/dist/js/leaflet.extra-markers.min.js';
import 'leaflet-extra-markers/dist/css/leaflet.extra-markers.min.css';
import axios from "axios";
import { Message } from '../utils/Message';
import { add } from "plotly.js-dist";

export default {
    name: "LocationMap",
    data() {
        return {
            map: null,
            markerGroup: null,
            markers: [],
            locations: []
        }
    },
    mounted() {
        this.map = L.map('map').setView([51.505, -0.09], 13);
        L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
            maxZoom: 19,
            attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(this.map);

        const apiKey = "53336b7385994b99aeed9e4d6bd2a072";
        let that = this;
        const addressSearchControl = L.control.addressSearch(apiKey, {
            resultCallback: that.onNewAddressSearch,
            mapViewBias: true,
        });
        //addControl Adds the given control to the map
        this.map.addControl(addressSearchControl);

        this.getLocations();
    },
    methods: {
        async getLocations() {
            try {
                const result = await axios.get('http://localhost:8080/api/locations');
                const locations = result.data;

                if (this.markerGroup)
                    this.markerGroup.clearLayers();
                this.markerGroup = L.layerGroup().addTo(this.map);

                this.markers = [];
                for (let index in locations) {
                    let loc = locations[index];
                    this.markers.push(L.marker([loc.latitude, loc.longitude]).addTo(this.markerGroup));
                }

                this.fitBounds();
            } catch (e) {
                Message.errorAxios(e);
            }
        },
        fitBounds() {
            if (this.markers.length == 0) return;

            const group = new L.featureGroup(this.markers);
            this.map.fitBounds(group.getBounds().pad(0.5));
        },
        onNewAddressSearch(address) {
            console.log(address)
            if (address == null)
                return;

            var redMarker = L.ExtraMarkers.icon({
                icon: 'location arrow icon',
                markerColor: 'red',
                shape: 'circle',
                prefix: ''
            });

            const coords = [address.lat, address.lon];
            L.marker(coords, { icon: redMarker }).addTo(this.map);

            if (address.bbox && address.bbox.lat1 !== address.bbox.lat2 && address.bbox.lon1 !== address.bbox.lon2) {
                this.map.fitBounds([[address.bbox.lat1, address.bbox.lon1], [address.bbox.lat2, address.bbox.lon2]], { padding: [100, 100] })
            } else {
                this.map.setView(coords, 15);
            }

            this.locations.push({
                coordinates: coords,
                address: address.formatted,
                bbox: {
                    lon1: address.bbox.lon1,
                    lat1: address.bbox.lat1,
                    lon2: address.bbox.lon2,
                    lat2: address.bbox.lat2
                }
            });

        },
        formatCoordinates(coordinates) {
            return `${coordinates[0].toFixed(6)}, ${coordinates[1].toFixed(6)}`;
        },
        zoomToLocation(location) {
            this.map.fitBounds([[location.bbox.lat1, location.bbox.lon1], [location.bbox.lat2, location.bbox.lon2]], { padding: [100, 100] });
        }
    }
}
</script>

<style scoped>
#map {
    /*width: 100%;*/
    /*display: inline;*/
    min-height: 600px;
    padding: 20px;
}

.row {
    padding-bottom: 1em;
}
</style>