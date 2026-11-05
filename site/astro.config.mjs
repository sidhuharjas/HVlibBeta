// @ts-check
import { defineConfig } from 'astro/config';
import starlight from '@astrojs/starlight';
import starlightThemeBlack from 'starlight-theme-black';
import sitemap from '@astrojs/sitemap';
import tailwindcss from '@tailwindcss/vite';

const siteUrl = 'https://sidhuharjas.github.io';
const base = '/HVlibBeta';
const repoUrl = 'https://github.com/sidhuharjas/HVlibBeta';
const tagline = 'Pollen and nectar detection for FTC robots.';

export default defineConfig({
  site: siteUrl,
  base,
  // There is no landing page: the site root goes straight to the docs.
  // Astro prefixes `base` to the source path but not the destination.
  redirects: {
    '/': `${base}/quickstart/`,
  },
  integrations: [
    sitemap(),
    starlight({
      components: {
        ThemeProvider: './src/components/ForceDarkTheme.astro',
        ThemeSelect: './src/components/EmptyComponent.astro',
      },
      plugins: [
        starlightThemeBlack({
          navLinks: [{ label: 'Quickstart', link: '/quickstart' }],
          disableDarkmodeToggle: true,
        }),
      ],
      title: 'HiveVision',
      favicon: '/favicon-48.png',

      head: [
        { tag: 'meta', attrs: { name: 'robots', content: 'index, follow' } },
        { tag: 'meta', attrs: { name: 'theme-color', content: '#000000' } },
        { tag: 'meta', attrs: { property: 'og:title', content: 'HiveVision' } },
        { tag: 'meta', attrs: { property: 'og:description', content: tagline } },
        { tag: 'meta', attrs: { property: 'og:image', content: `${siteUrl}${base}/og.png` } },
      ],

      customCss: ['./src/styles/global.css'],
      social: [{ icon: 'github', label: 'GitHub', href: repoUrl }],
      sidebar: [
        {
          label: 'Start',
          items: [{ label: 'Quickstart', slug: 'quickstart' }],
        },
        {
          label: 'Reference',
          items: [
            { label: 'Modules', slug: 'modules' },
            { label: 'Examples', slug: 'examples' },
            { label: 'Calibration', slug: 'calibration' },
            { label: 'API', link: '/api/' },
          ],
        },
      ],
    }),
  ],

  vite: {
    plugins: [tailwindcss()],
  },
});
