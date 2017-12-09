package com.project.six.treasurehunt;

/**
 * 게시글의 정보를 담아두기위한 class입니다.
 */

public class postContext {
    public String title; //게시글의 제목
    public String context1; //게시글이 나타내는 힌트
    public String context2; //보상 내용

    public String imageURL1; //힌트에 사용되는 이미지
    public String imageURL2; //보상에 사용되는 이미지

    public long starttime; //보물을 찾을수 있는 시작시각
    public long startDate; //날짜
    public long endTime;   //보물을 찾을수 있는 시간의 마지막
    public long endDate;  //날짜

    public String writerName; //작성자의 이름
    public String writerUID;  //작성자의 id
    public String finderUID; //발견자의 id
    public String firebaseKey; //firebase에 저장되는 key값

    public double altitude; //고도
    public double latitude; //경도
    public double longitude; //위도
    public boolean isfinded; //찾아졌는가

}
