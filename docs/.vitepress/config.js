import { defineConfig } from 'vitepress'

export default defineConfig({
  title: 'Bizflow ERP',
  description: 'Documentación del sistema ERP Bizflow',
  lang: 'es-ES',
  
  // Excluir carpetas internas del build
  srcExclude: [
    'sessions/**',
    'backups/**',
    'planning/**',
    'process/**',
    'json/**',
    'postman/**',
    'researching/**',
    'entity/**',
    'spring/**',
    'docker/**',
    'makefile/**',
    'guides/**',
    'README_FULL.md',
    'README.md',
    'INDEX.md',
    'credentials_system.md'
  ],

  // Ignorar enlaces localhost durante el build
  ignoreDeadLinks: [
    /^http:\/\/localhost/
  ],

  head: [
    ['link', { rel: 'icon', href: '/logo.svg' }]
  ],

  themeConfig: {
    logo: '/logo.svg',
    
    nav: [
      { text: 'Inicio', link: '/' },
      { text: 'Guía', link: '/guide/getting-started' },
      { text: 'API', link: '/api/' },
      { text: 'GitHub', link: 'https://github.com/jdomdev/bizflow-erp-springboot-react-docker' }
    ],

    sidebar: {
      '/guide/': [
        {
          text: 'Introducción',
          items: [
            { text: 'Empezando', link: '/guide/getting-started' },
            { text: 'Instalación', link: '/guide/installation' },
            { text: 'Arquitectura', link: '/guide/architecture' }
          ]
        },
        {
          text: 'Guías por Rol',
          items: [
            { text: 'Administrador', link: '/guide/roles/admin' },
            { text: 'Manager', link: '/guide/roles/manager' },
            { text: 'Usuario', link: '/guide/roles/user' }
          ]
        },
        {
          text: 'Desarrollo',
          items: [
            { text: 'Configuración Local', link: '/guide/dev/local-setup' },
            { text: 'Docker', link: '/guide/dev/docker' },
            { text: 'Testing', link: '/guide/dev/testing' },
            { text: 'Deployment', link: '/guide/dev/deployment' }
          ]
        }
      ],
      '/api/': [
        {
          text: 'API Reference',
          items: [
            { text: 'Autenticación', link: '/api/auth' },
            { text: 'Gastos', link: '/api/expenses' },
            { text: 'Empleados', link: '/api/employees' },
            { text: 'Nóminas', link: '/api/payroll' },
            { text: 'Usuarios', link: '/api/users' },
            { text: 'Cargos', link: '/api/positions' }
          ]
        }
      ]
    },

    socialLinks: [
      { icon: 'github', link: 'https://github.com/jdomdev/bizflow-erp-springboot-react-docker' }
    ],

    footer: {
      message: 'Licencia GNU GPL v3',
      copyright: 'Copyright © 2025-present Bizflow ERP'
    },

    search: {
      provider: 'local',
      options: {
        translations: {
          button: {
            buttonText: 'Buscar',
            buttonAriaLabel: 'Buscar'
          },
          modal: {
            noResultsText: 'No se encontraron resultados',
            resetButtonTitle: 'Limpiar búsqueda',
            footer: {
              selectText: 'Seleccionar',
              navigateText: 'Navegar'
            }
          }
        }
      }
    },

    outline: {
      label: 'En esta página'
    },

    docFooter: {
      prev: 'Anterior',
      next: 'Siguiente'
    },

    lastUpdated: {
      text: 'Última actualización'
    }
  }
})
