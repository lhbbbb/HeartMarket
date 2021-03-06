import React, { Component } from 'react'
import './UpdateUser.scss'
import axios from 'axios'
import SessionDelete from '../common/SessionDelete'

class UpdateUser extends Component {
  user = JSON.parse(window.sessionStorage.getItem('user') || '{}')
  state = {
    nickname: '',
    profile: '',
    address: '',
    imgfile: '',
    base64: ''
  }
  gangnam = [
    '개포1동',
    '개포2동',
    '개포4동',
    '논현1동',
    '논현2동',
    '대치1동',
    '대치2동',
    '대치4동',
    '도곡1동',
    '도곡2동',
    '삼성1동',
    '삼성2동',
    '세곡동',
    '수서동',
    '신사동',
    '압구정동',
    '역삼1동',
    '역삼2동',
    '일원1동',
    '일원2동',
    '일원본동',
    '청담동'
  ]

  componentDidMount() {
    // // console.log(this.user);
  }

  goback = () => {
    // this.props.history.goback();
    window.history.back()
  }

  fileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    // // console.log(e.target.files);
    const reader = new FileReader()

    reader.onloadend = () => {
      const base64 = reader.result
      if (base64) {
        this.setState({
          base64: base64.toString()
        })
      }
    }
    if (e.target.files) {
      reader.readAsDataURL(e.target.files[0])

      this.setState({
        profile: e.target.files[0]
      })
    }
  }

  changeProfile = () => {
    const file = new FormData()
    const nickname: string = this.user.nickname
    const email: string = this.user.email
    const address: string = this.user.uarea[0].address
    if (this.state.profile) {
      file.append('profile', this.state.profile)
      axios({
        method: 'put',
        url: 'http://13.125.55.96:8080/user/updateUser',
        headers: { 'Content-Type': 'multipart/form-data' },
        params: {
          address: address,
          email: email,
          nickname: nickname
        },
        data: file
      })
        .then(res => {
          // console.log(res.data.data.profileImg);
          this.user.profileImg = res.data.data.profileImg
          window.sessionStorage.removeItem('user')
          window.sessionStorage.setItem('user', JSON.stringify(this.user))
          alert('프로필 이미지를 수정했어요')
        })
        .catch(err => {
          // console.log(err);
          alert('이미지 수정 실패')
        })
    } else {
      alert('프로필 이미지를 선택해주세요.')
    }
  }

  changeNickname = (nickname: string) => {
    this.setState({
      nickname: nickname
    })
  }

  updateNickname = () => {
    const file = new FormData()
    file.append('profile', this.state.profile)
    if (this.state.nickname) {
      axios({
        method: 'put',
        url: 'http://13.125.55.96:8080/user/updateUser',
        params: {
          address: this.user.uarea[0].address,
          email: this.user.email,
          nickname: this.state.nickname
        }
      })
        .then(res => {
          this.user.nickname = this.state.nickname
          window.sessionStorage.removeItem('user')
          window.sessionStorage.setItem('user', JSON.stringify(this.user))
          this.setState({
            nickname: this.user.nickname
          })
          alert('닉네임이 변경되었습니다.')
        })
        .catch(err => {
          alert('닉네임 변경 실패')
          // console.log(err);
        })
    } else {
      alert('닉네임을 입력해주세요.')
    }
  }

  changeAddr = (addr: string) => {
    this.setState({
      address: addr
    })
  }

  updateAddr = () => {
    // const file = new FormData();
    // file.append("profile", this.state.profile);
    // console.log(this.user.profileImg);
    if (this.state.address) {
      axios({
        method: 'put',
        url: 'http://13.125.55.96:8080/user/updateUser',
        params: {
          address: this.state.address,
          email: this.user.email,
          nickname: this.user.nickname
        }
      })
        .then(res => {
          this.user.uarea[0].address = this.state.address
          window.sessionStorage.removeItem('user')
          window.sessionStorage.setItem('user', JSON.stringify(this.user))
          this.setState({
            address: this.user.address
          })
          alert('주소가 변경되었습니다.')
        })
        .catch(err => {
          alert('주소 변경 실패')
          // console.log(err);
        })
    } else {
      alert('동네를 선택해주세요.')
    }
  }

  render() {
    // // console.log(this.user);
    return (
      <div className="big">
        <SessionDelete></SessionDelete>
        <div className="updateuser">
          <h1>프로필 수정</h1>
          <div className="profile-img-wrapper">
            <div className="pro-img">
              {this.state.profile === '' ? <img className="profile-img" alt="profile" src={this.user.profileImg} /> : <img className="profile-img" alt="profile" src={this.state.base64} />}
            </div>

            <br />
            {/* <label for="profile-img">이미지 선택</label> */}
            <input type="file" name="profile" id="profile" className="profile" onChange={this.fileChange} />
            <button className="btn-updateprofile" type="button" onClick={this.changeProfile}>
              이미지 수정하기
            </button>
          </div>
          <br />
          <br />
          <div className="section">
            <h3>닉네임</h3>
            <p>최대 8글자까지 가능해요</p>
            <input type="text" className="input-nickname" placeholder={this.user.nickname} onChange={e => this.changeNickname(e.target.value)} />
            <button className="btn-updatenick" onClick={this.updateNickname}>
              수정하기
            </button>
          </div>
          <div className="section">
            <h3>우리 동네</h3>
            <p>동네를 수정하세요</p>
            {/* <input type="text" className="input-address" placeholder={this.user.uarea[0].address} onChange={e => this.changeAddr(e.target.value)} /> */}
            <select className="address" onChange={e => this.changeAddr(e.target.value)}>
              <option value="" selected>
                동네 설정하기
              </option>
              {this.gangnam.map(dong => {
                return <option>{dong}</option>
              })}
            </select>

            <button className="btn-updatenick" onClick={this.updateAddr}>
              수정하기
            </button>
          </div>
          <br />
          {/* 마이페이지로 돌아가도록 */}
          <button className="btn-update" onClick={this.goback}>
            수정 완료하고 마이페이지로 가기
          </button>
        </div>
      </div>
    )
  }
}

export default UpdateUser
