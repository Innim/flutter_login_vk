import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_login_vk/flutter_login_vk.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  final plugin = VKLogin(debug: true);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _sdkVersion;
  VKAccessToken _token;
  VKUserProfile _profile;
  String _email;

  @override
  void initState() {
    super.initState();

    _getSdkVersion();
    _updateLoginInfo();
  }

  @override
  Widget build(BuildContext context) {
    final isLogin = _token != null && _profile != null;
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Login via VK example'),
        ),
        body: Padding(
          padding: const EdgeInsets.symmetric(vertical: 18.0, horizontal: 8.0),
          child: Center(
            child: Column(
              children: <Widget>[
                if (_sdkVersion != null) Text("SDK v$_sdkVersion"),
                if (isLogin)
                  Padding(
                    padding: const EdgeInsets.only(bottom: 10),
                    child: _buildUserInfo(context, _profile, _token, _email),
                  ),
                isLogin
                    ? OutlineButton(
                        child: Text('Log Out'),
                        onPressed: _onPressedLogOutButton,
                      )
                    : OutlineButton(
                        child: Text('Log In'),
                        onPressed: _onPressedLogInButton,
                      ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildUserInfo(BuildContext context, VKUserProfile profile,
      VKAccessToken accessToken, String email) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text('User: '),
        Text(
          '${profile.firstName} ${profile.lastName}',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        Text(
          'Online: ${profile.online}, Online mobile: ${profile.onlineMobile}',
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        Image.network(profile.photo200),
        Text('AccessToken: '),
        Container(
          child: Text(
            accessToken.token,
            softWrap: true,
          ),
        ),
        Text('Created: ${accessToken.created}'),
        Text('Expires in: ${accessToken.expiresIn}'),
        if (email != null) Text('Email: $email'),
      ],
    );
  }

  void _onPressedLogInButton() async {
    await widget.plugin.logIn(scope: [
      VKScope.email,
    ]);
    _updateLoginInfo();
  }

  void _onPressedLogOutButton() async {
    await widget.plugin.logOut();
    _updateLoginInfo();
  }

  void _getSdkVersion() async {
    final sdkVersion = await widget.plugin.sdkVersion;
    setState(() {
      _sdkVersion = sdkVersion;
    });
  }

  void _updateLoginInfo() async {
    final plugin = widget.plugin;
    final token = await plugin.accessToken;
    final profile = await plugin.getUserProfile();
    final email = await plugin.getUserEmail();
    setState(() {
      _token = token?.data;
      _profile = profile?.data;
      _email = email;
    });
  }
}
