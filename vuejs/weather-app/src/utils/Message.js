import iziToast from 'izitoast'
import 'izitoast/dist/css/iziToast.min.css'

class Message {
  static info(msg) {
    iziToast.info({
      title: 'Info',
      position: 'center',
      message: msg
    })
  }

  static error(msg) {
    iziToast.error({
      title: 'Error',
      position: 'center',
      message: msg
    })
  }

  static errorAxios(error) {
    let msg = error.message

    if (error.response.data.message) msg += ' <br>Cause: ' + error.response.data.message
    iziToast.error({
      title: 'Error',
      position: 'center',
      message: msg
    })
  }
}

export { Message }
