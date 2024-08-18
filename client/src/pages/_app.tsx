import { Provider } from 'react-redux';
import { createTheme, ThemeProvider } from '@material-ui/core';
import { store } from 'store';
import { Layout } from 'components/Layout';
import 'styles/styles.scss';

const theme = createTheme({
  palette: {
    primary: { main: '#000' },
    secondary: { main: '#FFF' },
  },
});

function MyApp({ Component, pageProps }) {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <Layout title={Component.pageTitle} subtitle={Component.pageSubtitle}>
          <Component {...pageProps} />
        </Layout>
      </ThemeProvider>
    </Provider>
  );
}

export default MyApp;
