# https://auth0.com/docs/api/authentication#get-user-info

from aiohttp import web
import json

auth_credentials = {}
app = web.Application()

def read_json(path):
    with open(path, "r") as f:
        json_data = json.loads(f.read())
    return json_data


async def userinfo(request):
    authorization = request.headers.get("Authorization")
    if (not(authorization)):
        raise web.HTTPBadRequest()
    token = authorization.split()[1]
    user_info = auth_credentials.get(token)
    if (not(user_info)):
        raise web.HTTPNotFound()

    return web.json_response(user_info)


if __name__ == '__main__':
    auth_credentials = read_json("./resources/userinfo.json")
    config = read_json("./resources/config.json")
    app.add_routes([web.get('/api/userinfo', userinfo)])
    web.run_app(app, port=config.get("port"))
