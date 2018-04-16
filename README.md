# SimpleNetwork
基于Retrofit Rxjava Okhttp封装的网络框架，符合业务需求，使用简单方便
```
NetworkClient
             .post("user")
             .params("username", "changmu175")
             .execute(new RequestListener<UserResult>() {
                  @Override
                  public void onFailure(BaseException e) {

                  }

                  @Override
                  public void onSuccess(UserResult apiResult) {

                  }
              });
```

内部做了数据的安全处理，后台返回的数据都有默认值，不会返回null。
