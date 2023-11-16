import { createStore } from 'vuex'
import axios from 'axios'
import { Message } from '../utils/Message'

export const store = createStore({
  state() {
    return {
      locations: []
    }
  },
  mutations: {
    setLocations(state, newLocations) {
      state.locations = newLocations
    }
  },
  actions: {
    async fetchLocations({ commit }) {
      try {
        const result = await axios.get('http://localhost:8080/api/locations')
        commit('setLocations', result.data)
      } catch (e) {
        Message.errorAxios(e)
      }
    },
    async createNewLocation({ commit }, locationObject) {
      try {
        await axios.post('http://localhost:8080/api/location', locationObject)
      } catch (e) {
        Message.errorAxios(e)
      }
    }
  }
})
