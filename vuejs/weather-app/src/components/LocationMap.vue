<template>
    <div class="ui container fluid">
        <div class="ui grid">
            <div class="row">
                <div class="twelve wide column">
                    <h2 class="ui header">Map</h2>
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
                                <td>{{ formatCoordinates(loc) }}</td>
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
                    <input class="ui button primary" type="button" value="Get Locations" @click="showLocations(true)" />
                </div>
            </div>
        </div>
    </div>

    <div class="ui modal" id="location-map-modal">
        <i class="close icon"></i>
        <div class="header">
            Modal Title
        </div>
        <div class="actions">
            <div class="ui cancel button">Cancel</div>
            <div class="ui ok button">OK</div>
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
import { Message } from '../utils/Message';
import { mapState } from 'vuex';


export default {
    name: "LocationMap",
    data() {
        return {
            map: null,
            markerGroup: null,
            markerGroupNewLocations: null,
            markers: [],
            newAddress: {}
        }
    },
    computed: {
        ...mapState(['locations'])
    },
    mounted() {
        this.map = L.map('map').setView([47.69, 13.34], 7);
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

        this.showLocations(true);
    },
    methods: {
        async showLocations(doFitBounds) {
            try {
                await this.$store.dispatch('fetchLocations');

                if (this.markerGroup)
                    this.markerGroup.clearLayers();
                if (this.markerGroupNewLocations)
                    this.markerGroupNewLocations.clearLayers();
                this.markerGroup = L.layerGroup().addTo(this.map);
                this.markerGroupNewLocations = L.layerGroup().addTo(this.map);

                this.markers = [];
                for (let index in this.locations) {
                    let loc = this.locations[index];
                    this.markers.push(L.marker([loc.latitude, loc.longitude]).addTo(this.markerGroup));
                }

                if(doFitBounds)
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
            if (address == null) {
                Message.info('Location not found');
                return;
            }

            if (this.markerGroupNewLocations)
                this.markerGroupNewLocations.clearLayers();

            const redMarker = L.ExtraMarkers.icon({
                icon: 'location arrow icon',
                markerColor: 'red',
                shape: 'circle',
                prefix: ''
            });

            const coords = [address.lat, address.lon];

            L.marker(coords, { icon: redMarker }).addTo(this.markerGroupNewLocations);

            if (address.bbox && address.bbox.lat1 !== address.bbox.lat2 && address.bbox.lon1 !== address.bbox.lon2) {
                this.map.fitBounds([[address.bbox.lat1, address.bbox.lon1], [address.bbox.lat2, address.bbox.lon2]], { padding: [100, 100] })
            } else {
                this.map.setView(coords, 15);
            }

            this.newAddress = address;
            let that = this;
            $('.ui.modal')
                .modal({
                    inverted: true,
                    closable: false,
                    onDeny: function () {
                        return true;
                    },
                    onApprove: async function () {
                        const address = this.newAddress;
                        const newLocation = {
                            latitude: address.lat,
                            longitude: address.lon,
                            address: address.formatted
                        };
                        await this.$store.dispatch('createNewLocation', newLocation);
                        this.showLocations(false);
                    }.bind(this)
                })
                .modal('show');
        },
        formatCoordinates(location) {
            return `${location.latitude.toFixed(6)}, ${location.longitude.toFixed(6)}`;
        },
        zoomToLocation(location) {
            this.map.setView([location.latitude, location.longitude], 13);
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