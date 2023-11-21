import moment from 'moment/moment'

const mapMonths = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec'
]

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
    return moment(date).format('YYYY-MM-DD')
  },

  listOfMonthsToStrings(months) {
    let monthAsStrings = []
    for(let m of months) {
      monthAsStrings.push(mapMonths[m-1])
    }
    return monthAsStrings
  }
}
