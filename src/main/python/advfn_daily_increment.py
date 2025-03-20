
import requests
from datetime import datetime

from mongo import Database

# https://br.advfn.com/common/api/charts/GetHistory?symbol=BOV%5EBBAS3&frequency=5&resolution=1M&to=1742074214&volume=true&afterhours=false&errorMode=true

BASE_URL = 'https://br.advfn.com/common/api/charts/GetHistory?'

symbols = ['BBAS3', 'ITUB4', 'ITSA4', 'BPAN4', 'BBDC4', 'BPAC11', 'SANB11', 'B3SA3']

FREQUENCY_MINUTES = '1'

RESOLUTION = '1D'

to = str(int(datetime.now().timestamp()))

VOLUME = 'true'
AFTER_HOURS = 'true'
ERROR_MODE = 'true'

def format_doc(symbol: str, data: dict):
    docs = []

    values = zip(
        data.get('result').get('data').get('t'), # timestamp
        [('open', i) for i in data.get('result').get('data').get('o')], # open
        [('high', i) for i in data.get('result').get('data').get('h')], # high
        [('low', i) for i in data.get('result').get('data').get('l')], # low
        [('close', i) for i in data.get('result').get('data').get('c')], # close
        [('volume', i) for i in data.get('result').get('data').get('v')] # volume
    )

    for i in values:
        for k in i:
            if isinstance(k, tuple):
                docs.append(
                        {
                            'timestamp': datetime.fromtimestamp(i[0]),
                            'metadata': {
                                'symbol': symbol,
                                'type': k[0]
                            },
                            'value': k[1]
                        }
                )
        
    return docs

for i in symbols:

    URL = f'''{BASE_URL}symbol=BOV%5E{i}&frequency={FREQUENCY_MINUTES}&resolution={RESOLUTION}&to={to}&volume={VOLUME}&afterhours={AFTER_HOURS}&errorMode={ERROR_MODE}'''

    print(datetime.now())

    data = requests.get(
        URL, 
        headers={
            'Content-Type': 'application/json',
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36 Edg/134.0.0.0'
        }
    )

    db = Database('LeapIntoTSDBApi')

    db.insert('stocks', format_doc(i, data.json()))
