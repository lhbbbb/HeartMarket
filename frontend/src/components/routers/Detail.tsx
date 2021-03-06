import React from 'react'
import Header from '../common/Header'
import Nav from '../common/Nav'
import SlideImg from '../common/SlideImg'
import Footer from '../common/Footer'
import axios from 'axios'
import './Detail.scss'
import Zzim from '../common/Zzim'
import { Link, Redirect } from 'react-router-dom'
import Modal from '../alarm/AlarmModal'
import { connect } from 'react-redux'
import { RootState } from '../../modules'
import TopButton from '../common/TopButton'
import PenButton from '../common/PenButton'
import TradeModal from './trade/TradeModal'

interface Props {
  status: string | null
}

interface Bcandidate {
  nickname: string
  email: string
}

class Detail extends React.Component<Props> {
  user = JSON.parse(window.sessionStorage.getItem('user') || '{}')

  state = {
    all: {
      trade: {
        tradeNo: 0,
        tradeTitle: '',
        tradeCategory: '',
        tradeArea: '',
        tTradeImg: [{ imgNo: 0, tiTrade: 0, orgImg: '' }],
        productInfo: '',
        productPrice: '',
        tuser: { userNo: 0, nickname: '', profileImg: '', email: '' },
        buser: ''
      },
      cno: 0,
      complete: 0,
      heartguage: 0
    },
    num: '',
    isModalOpen: false,
    bcandidate: Array<Bcandidate>(),
    success: ''
  }

  category = [
    { name: '' },
    { name: '디지털/가전' },
    { name: '가구/인테리어' },
    { name: '유아동/유아도서' },
    { name: '생활가공식품' },
    { name: '여성의류' },
    { name: '여성잡화' },
    { name: '뷰티/미용' },
    { name: '남성패션/잡화' },
    { name: '스포츠/레저' },
    { name: '게임/취미' },
    { name: '도서/티켓/음반' },
    { name: '반려동물용품' },
    { name: '기타중고물품' }
  ]

  updateUrl = () => {
    const url = window.location.href.split('/')
    const num = url[url.length - 1]
    var email = 'none'
    if (this.user.email !== undefined && this.user.email !== '') email = this.user.email
    //if(email === undefined) email = "none";
    this.setState({
      num: num
    })

    axios({
      method: 'get',
      url: 'http://13.125.55.96:8080/trade/' + num,
      params: {
        email: email
      }
    })
      .then(res => {
        // // console.log("all", res.data);
        const all = res.data
        //alert("받아는 옴")
        this.setState({
          all
        })

        // // console.log("trade", this.state.all);

        if (this.user.userNo === this.state.all.trade.tuser.userNo) {
          axios({
            method: 'get',
            url: 'http://13.125.55.96:8080/user/nickname',
            params: {
              tradeNo: num,
              userNo: this.user.userNo
            }
          })
            .then(res => {
              // // console.log('bcandi',res.data.data);
              this.setState({
                bcandidate: res.data.data
              })
            })
            .catch(err => {
              alert('후보를 불러오지 못했습니다.')
            })
        }
      })
      .catch(err => {
        // // console.log("err", err);
        alert('error')
      })
  }

  openModal = () => {
    if (this.user.email === undefined) {
      alert('로그인을 해주세요')
      return
    }
    this.setState({
      isModalOpen: true
    })
  }
  closeModal = () => {
    this.setState({
      isModalOpen: false
    })
  }

  componentDidMount() {
    window.scrollTo(0, 0)
    this.updateUrl()
    window.sessionStorage.setItem('isText', 'true')
  }
  componentWillUnmount() {
    window.sessionStorage.setItem('isText', 'false')
  }
  componentWillReceiveProps() {
    this.updateUrl()
  }

  completeTrade = () => {
    this.setState({
      isModalOpen: true
    })
    if (!this.state.bcandidate) {
      alert('구매 대상자가 없어서 거래를 완료할 수 없어요')
    }
  }

  deleteTrade = () => {
    const url = window.location.href.split('/')
    const num = url[url.length - 1]
    axios({
      method: 'delete',
      url: 'http://13.125.55.96:8080/trade/delete/' + num
    })
      .then(res => {
        // // console.log(res);
        // alert("게시물이 삭제되었습니다.");
        this.setState({
          success: '/'
        })
      })
      .catch(err => {
        // // console.log(err);
        alert('게시물 삭제에 실패했습니다.')
      })
  }

  render() {
    if (this.state.success) {
      alert('글이 삭제되었습니다')
      return <Redirect to={this.state.success}></Redirect>
    }
    return (
      <div>
        <Header />
        <Nav></Nav>
        <div className="product-detail">
          {/* <hr /> */}
          <br />
          <div className="detail-grid">
            <div className="detail-l">
              {/* <img src={this.state.trade.ttradeImg[0].orgImg} alt="" /> */}
              <SlideImg ttradeImg={this.state.all.trade.tTradeImg} />
            </div>
            <div className="detail-r">
              <div className="tuser-info">
                <div className="tuser-profile">
                  <img src={this.state.all.trade.tuser.profileImg} alt="profile" />
                </div>
                <div className="tuser-id">
                  <h3>
                    <Link to={`/user/${this.state.all.trade.tuser.userNo}`}>{this.state.all.trade.tuser.nickname}</Link>
                  </h3>
                  <h4>{this.state.all.trade.tradeArea}</h4>
                </div>
                <div className="tuser-manners">
                  <h3>{this.state.all.heartguage}</h3>
                  <h3>BPM</h3>
                </div>
              </div>
              {this.state.all.complete === 1 ? (
                <div className="complete-trade">
                  <h1>거래 완료!</h1>
                </div>
              ) : (
                <></>
              )}
              <div className="trade">
                <h4>카테고리 > {this.category[Number(this.state.all.trade.tradeCategory)].name}</h4>
                <h2>{this.state.all.trade.tradeTitle}</h2>
                <br />
                <h3>{this.state.all.trade.productPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')}원</h3>
              </div>
              <br />
              <br />
              <br />
              {this.user.email === this.state.all.trade.tuser.email ? (
                <div className="tuser-btn">
                  <button className="btn-complete" onClick={this.completeTrade} disabled={this.state.all.complete === 1}>
                    거래완료
                  </button>
                  <button className="btn-delete" onClick={this.deleteTrade}>
                    삭제
                  </button>
                  <Link
                    to={{
                      pathname: '/write/update',
                      state: { props: this.state.all.trade }
                    }}
                  >
                    <button className="btn-update">수정</button>
                  </Link>
                  {this.state.bcandidate ? (
                    <TradeModal
                      tradeNo={this.state.all.trade.tradeNo}
                      email={this.state.all.trade.tuser.email}
                      isOpen={this.state.isModalOpen}
                      close={this.closeModal}
                      bcandidate={this.state.bcandidate}
                    />
                  ) : (
                    <></>
                  )}
                </div>
              ) : (
                <div className="bottom">
                  <button className="btn-heart">
                    <Zzim num={this.state.num} cno={this.state.all.cno} uno={this.state.all.trade.tuser.userNo}></Zzim>
                  </button>

                  <button className="btn-contact" onClick={this.openModal}>
                    쪽지 보내기
                  </button>
                  <Modal
                    tradeNo={this.state.all.trade.tradeNo}
                    email={this.state.all.trade.tuser.email}
                    nickname={this.state.all.trade.tuser.nickname}
                    isOpen={this.state.isModalOpen}
                    close={this.closeModal}
                  />
                </div>
              )}
            </div>
          </div>
          <div className="product-info">
            <h3>
              <span>상품 상세 설명</span>
            </h3>
            <h3>{this.state.all.trade.productInfo}</h3>
          </div>
        </div>
        <TopButton />
        <PenButton />
        <Footer />
      </div>
    )
  }
}

// export default Detail;
export default connect(({ userStatus }: RootState) => ({
  status: userStatus.status
}))(Detail)
