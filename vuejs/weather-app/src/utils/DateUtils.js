import moment from "moment/moment"

export default {
  getDateMonthYear(year, month) {
    let date = new Date()
    date.setFullYear(year, month, 1)
    date.setHours(0)
    date.setMinutes(0)
    date.setSeconds(0)
    date.setMilliseconds(0)

    return date
  },

  /**
   * 
   * @param {Date} date 
   */
  dateToString(date) {
    console.log(date)
    return moment(date).format('YYYY-MM-DD')
  }
}
